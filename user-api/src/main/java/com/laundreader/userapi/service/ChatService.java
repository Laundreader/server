package com.laundreader.userapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laundreader.common.error.ErrorMessage;
import com.laundreader.common.error.exception.Exception500;
import com.laundreader.common.util.JsonExtractor;
import com.laundreader.external.clova.ClovaStudioClient;
import com.laundreader.external.clova.service.ClovaStudioService;
import com.laundreader.external.clova.service.response.AssistantSuggestionEvent;
import com.laundreader.external.clova.service.response.SummaryResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
	private static final int MAX_MODEL_OUTPUT_TOKENS = 4096; // 모델 max 출력 토큰
	private static final int SUMMARY_THRESHOLD = 32000; // 요약 임계값. total max 32000
	private final ClovaStudioService clovaStudioService;
	private final JsonExtractor jsonExtractor;
	private final ObjectMapper objectMapper;
	private final RedisTemplate<String, String> redisTemplate;

	public Flux<ServerSentEvent<String>> streamChat(String sessionId, String message) {
		// Redis에서 기존 대화 불러오기
		String conversationKey = "chat:" + sessionId;
		List<String> existingConversation = Optional.ofNullable(
				redisTemplate.opsForList().range(conversationKey, 0, -1))
			.orElse(new ArrayList<>());
		AtomicInteger existingTokens = new AtomicInteger(
			Optional.ofNullable(redisTemplate.opsForValue().get(conversationKey + ":tokens"))
				.map(Integer::parseInt).orElse(0)
		);

		// 새 메시지 토큰 계산
		int newMessageTokens = clovaStudioService.getTokenCount(message, ClovaStudioClient.HCX_DASH_002);
		if ((existingTokens.get() + newMessageTokens + MAX_MODEL_OUTPUT_TOKENS) > SUMMARY_THRESHOLD) {
			// 요약 후 토큰 수 포함 반환
			SummaryResult summaryResult = clovaStudioService.summarizeConversation(existingConversation);
			String summaryText = "SUMMARY: 이전 대화 기록 요약입니다.\n" + summaryResult.getText();
			int tokenCount = summaryResult.getTokenCount();

			// Redis 저장
			redisTemplate.delete(conversationKey);
			redisTemplate.opsForList().rightPush(conversationKey, summaryText);
			redisTemplate.opsForValue().set(conversationKey + ":tokens", String.valueOf(tokenCount));

			// existingConversation 갱신
			existingConversation = new ArrayList<>();
			existingConversation.add(summaryText);

			// existingTokens 갱신
			existingTokens.set(tokenCount);
		}

		// 새 사용자 메시지 Redis에 저장
		redisTemplate.opsForList().rightPush(conversationKey, "USER: " + message);

		StringBuilder redisBuffer = new StringBuilder();   // 원본 누적용
		StringBuilder paragraphBuffer = new StringBuilder(); // 문단 단위 SSE용

		// ClovaStudioService로 이전 대화+새 메시지로 챗봇 호출 → 스트리밍 반환
		return clovaStudioService.laundryChatbot(existingConversation, message)
			.flatMap(event -> handleSseEvent(event, paragraphBuffer, redisBuffer, existingTokens, conversationKey))
			.concatWith(Flux.defer(() -> flushRemainingParagraph(paragraphBuffer, redisBuffer)))
			.doFinally(signal -> finalizeResponse(signal, conversationKey, redisBuffer));
	}

	/*
	 * SSE 이벤트 처리 함수
	 * */
	private Flux<ServerSentEvent<String>> handleSseEvent(
		ServerSentEvent<String> event,
		StringBuilder paragraphBuffer,
		StringBuilder redisBuffer,
		AtomicInteger totalTokens,
		String conversationKey
	) {
		String eventName = event.event();
		String data = event.data();

		if ("token".equals(eventName) && data != null && !data.isBlank()) {
			return processTokenEvent(data, paragraphBuffer, redisBuffer);
		} else if ("result".equals(eventName) && data != null) {
			processResultEvent(data, totalTokens, conversationKey);
		}

		return Flux.empty();
	}

	/*
	 * result 이벤트 처리: 토큰 누적
	 * */
	private void processResultEvent(String data, AtomicInteger totalTokens, String conversationKey) {
		try {
			JsonNode node = objectMapper.readTree(data);
			JsonNode usageNode = node.at("/usage/totalTokens");
			if (!usageNode.isMissingNode()) {
				totalTokens.addAndGet(usageNode.asInt());
				redisTemplate.opsForValue().set(conversationKey + ":tokens", String.valueOf(totalTokens.get()));
				log.info("{}:tokens = {}", conversationKey, String.valueOf(totalTokens.get()));
			}
		} catch (Exception ignored) {
		}
	}

	/*
	 * token 이벤트 처리: 문단 누적 및 SSE 전송
	 * */
	private Flux<ServerSentEvent<String>> processTokenEvent(
		String data,
		StringBuilder paragraphBuffer,
		StringBuilder redisBuffer
	) {
		String content = extractContent(data);
		paragraphBuffer.append(content);

		List<ServerSentEvent<String>> events = new ArrayList<>();
		int index;
		while ((index = paragraphBuffer.indexOf("\n\n")) >= 0) {
			String paragraph = paragraphBuffer.substring(0, index).trim();
			if (!paragraph.isBlank()) {
				redisBuffer.append(paragraph).append("\n\n");
				events.addAll(convertParagraphToSSE(paragraph));
			}
			paragraphBuffer.delete(0, index + 2);
		}
		return Flux.fromIterable(events);
	}

	// 스트리밍 응답 JSON에서 실제 content만 뽑는 헬퍼
	private String extractContent(String rawJson) {
		try {
			JsonNode node = objectMapper.readTree(rawJson);
			JsonNode contentNode = node.at("/message/content");
			if (contentNode.isMissingNode()) {
				return "";
			}
			return contentNode.asText();
		} catch (Exception e) {
			log.warn("Failed to parse streaming chunk: {}", rawJson);
			return "";
		}
	}

	/*
	 * 남은 문단 flush
	 * */
	private Flux<ServerSentEvent<String>> flushRemainingParagraph(StringBuilder paragraphBuffer,
		StringBuilder redisBuffer) {
		String remaining = paragraphBuffer.toString().trim();
		paragraphBuffer.setLength(0);
		if (!remaining.isBlank()) {
			redisBuffer.append(remaining).append("\n\n");
			return Flux.fromIterable(convertParagraphToSSE(remaining));
		}
		return Flux.empty();
	}

	/*
	 * 응답 종료 시 처리
	 * */
	private void finalizeResponse(SignalType signal, String conversationKey, StringBuilder redisBuffer) {
		if (signal == SignalType.ON_COMPLETE) {
			redisTemplate.opsForList()
				.rightPush(conversationKey, "ASSISTANT: " + redisBuffer.toString().trim());
			log.info("응답 수신 종료. redis에 저장 ASSISTANT: {}", redisBuffer.toString().trim());
			redisBuffer.setLength(0);
		}
	}

	// 텍스트, JSON 유형에 따라 SSE event 생성.
	private List<ServerSentEvent<String>> convertParagraphToSSE(String paragraph) {
		List<ServerSentEvent<String>> events = new ArrayList<>();

		if (paragraph == null || paragraph.isBlank()) {
			return events;
		}

		String jsonBlock = null;

		// 1. ```json … ``` 블록 우선 추출
		int startIdx = paragraph.indexOf("```json");
		if (startIdx >= 0) {
			int endIdx = paragraph.indexOf("```", startIdx + 6);
			if (endIdx > startIdx) {
				String potentialJson = paragraph.substring(startIdx + 6, endIdx).trim();
				try {
					// 유효한 JSON인지 검증
					jsonBlock = jsonExtractor.extractValidJsonBlock(potentialJson);
					// 원본 paragraph에서 ```json … ``` 블록 전체 제거
					paragraph = paragraph.substring(0, startIdx) + paragraph.substring(endIdx + 3);
				} catch (Exception ignored) {
				}
			}
		}

		// 2. ```json … ``` 없으면 일반 JSON 추출
		if (jsonBlock == null) {
			try {
				jsonBlock = jsonExtractor.extractValidJsonBlock(paragraph);
				// 추출된 JSON 제거
				paragraph = paragraph.replace(jsonBlock, "");
			} catch (Exception ignored) {
			}
		}

		// 3. 문단에서 JSON 제거 후 남은 텍스트 이벤트 생성 {"message":"..."} 로 감싸서 보냄
		String textPart = paragraph.trim();
		if (!textPart.isBlank()) {
			try {
				events.add(ServerSentEvent.<String>builder()
					.event("assistant-answers")
					.data(objectMapper.writeValueAsString(Map.of("message", textPart)))
					.build());
			} catch (JsonProcessingException e) {
				throw new Exception500(ErrorMessage.INTERNAL_ERROR + "text to json");
			}
		}

		// 마지막 JSON 이벤트 생성
		if (jsonBlock != null) {
			tryParseAssistantSuggestion(jsonBlock).ifPresent(suggestion -> {
				try {
					Map<String, Object> map = objectMapper.convertValue(suggestion, Map.class);
					map.remove("type");

					events.add(ServerSentEvent.<String>builder()
						.event("assistant-suggestions")
						.data(objectMapper.writeValueAsString(map))
						.build());
				} catch (JsonProcessingException e) {
					throw new Exception500(ErrorMessage.INTERNAL_ERROR + "text to json");
				}
			});
		}

		return events;
	}

	// 챗봇 추천 목록 파싱
	private Optional<AssistantSuggestionEvent> tryParseAssistantSuggestion(String json) {
		try {
			AssistantSuggestionEvent event = objectMapper.readValue(json, AssistantSuggestionEvent.class);
			if ("assistant-suggestions".equals(event.getType())) {
				return Optional.of(event);
			}
		} catch (Exception ignored) {
			// 파싱 실패 → 그냥 무시
		}
		return Optional.empty();
	}
}
