package com.laundreader.external.clova.service;

import java.util.List;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laundreader.common.error.ErrorMessage;
import com.laundreader.common.error.exception.Exception500;
import com.laundreader.common.util.JsonExtractor;
import com.laundreader.common.util.PromptUtils;
import com.laundreader.common.util.TrueFalseExtractor;
import com.laundreader.external.clova.client.ClovaStudioClient;
import com.laundreader.external.clova.dto.HamperSolutionDTO;
import com.laundreader.external.clova.dto.LaundryAnalysisDTO;
import com.laundreader.external.clova.dto.SingleSolutionDTO;
import com.laundreader.external.clova.dto.SummaryResultDTO;
import com.laundreader.external.clova.request.ClovaChatMessageBuilder;
import com.laundreader.external.clova.request.ClovaChatRequest;
import com.laundreader.external.clova.request.ClovaThinkingMessageBuilder;
import com.laundreader.external.clova.request.ClovaThinkingRequest;
import com.laundreader.external.clova.response.ClovaChatResponse;
import com.laundreader.external.clova.response.ClovaChatTokenizeResponse;
import com.laundreader.external.clova.response.ClovaThinkingResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClovaStudioService {
	private final ClovaStudioClient client;
	private final JsonExtractor jsonExtractor;
	private final ObjectMapper objectMapper;

	public boolean imageAnalysis(String analysisType, String base64) {
		String systemPrompt = PromptUtils.loadPrompt("prompt/system/image-analysis-prompt.md");

		// Request
		ClovaChatRequest request = new ClovaChatMessageBuilder()
			.addSystemMessage(systemPrompt)
			.addUserMessage(analysisType, base64)
			.build();

		// Response
		ClovaChatResponse clovaChatResponse = client.callChatHCX005(request);
		log.info("clova 이미지 검증 Response: " + clovaChatResponse.getResult().getMessage().getContent());
		return TrueFalseExtractor.extractFirstTrueOrFalse(clovaChatResponse.getResult().getMessage().getContent());
	}

	public LaundryAnalysisDTO laundryAnalysis(String ocrText, String labelBase64, String clothesBase64) {
		String systemPrompt = PromptUtils.loadPrompt("prompt/system/laundry-analysis-prompt.md");
		String userFinalTurnPrompt = PromptUtils.loadPrompt("prompt/user/laundry-analysis-prompt.md");

		// Request
		ClovaChatRequest request = new ClovaChatMessageBuilder()
			.addSystemMessage(systemPrompt)
			.addUserMessage(ocrText, clothesBase64)
			.addUserMessage(userFinalTurnPrompt, labelBase64)
			.build();

		// Response
		ClovaChatResponse clovaChatResponse = client.callChatHCX005(request);
		log.info("clova 세탁물 분석 Response: " + clovaChatResponse.getResult().getMessage().getContent());

		// json 형식의 답변이 왔는지 검증 후 object로 변환하여 return
		try {
			String jsonText = jsonExtractor.extractValidJsonBlock(
				clovaChatResponse.getResult().getMessage().getContent());
			return objectMapper.readValue(jsonText, LaundryAnalysisDTO.class);
		} catch (JsonProcessingException e) {
			log.error("clova 세탁물 분석 json 추출 실패: " + e.getMessage());
			throw new Exception500(ErrorMessage.CLOVA_STUDIO_RESPONSE_PARSING_FAILED);
		}
	}

	// 단일 세탁 솔루션
	public SingleSolutionDTO laundrySolutionSingle(String inputData) {
		String systemPrompt = PromptUtils.loadPrompt("prompt/system/laundry-solution-single-prompt.md");

		// Request
		ClovaChatRequest request = new ClovaChatMessageBuilder()
			.addSystemMessage(systemPrompt)
			.addUserMessage(inputData)
			.build();

		// Response
		ClovaChatResponse clovaChatResponse = client.callChatHCX005(request);
		log.info("clova 단일 세탁 솔루션 Response: " + clovaChatResponse.getResult().getMessage().getContent());

		// json 형식의 답변이 왔는지 검증 후 object로 변환하여 return
		try {
			String jsonText = jsonExtractor.extractValidJsonBlock(
				clovaChatResponse.getResult().getMessage().getContent());
			return objectMapper.readValue(jsonText, SingleSolutionDTO.class);
		} catch (JsonProcessingException e) {
			log.error("clova 단일 세탁 솔루션 json 추출 실패: " + e.getMessage());
			throw new Exception500(ErrorMessage.CLOVA_STUDIO_RESPONSE_PARSING_FAILED);
		}
	}

	// 빨래 바구니 세탁 솔루션
	public HamperSolutionDTO laundrySolutionHamper(String inputData) {
		String systemPrompt = PromptUtils.loadPrompt("prompt/system/laundry-solution-hamper-prompt.md");

		// Request
		ClovaThinkingRequest clovaThinkingRequest = new ClovaThinkingMessageBuilder()
			.addSystemMessage(systemPrompt)
			.addUserMessage(inputData)
			.build();

		// Response
		ClovaThinkingResponse clovaThinkingResponse = client.callThinkingHCX007(clovaThinkingRequest);
		log.info("clova 빨래 바구니 세탁 솔루션 Response: " + clovaThinkingResponse.getResult().getMessage().getContent());

		// json 형식의 답변이 왔는지 검증 후 object로 변환하여 return
		try {
			String jsonText = jsonExtractor.extractValidJsonBlock(
				clovaThinkingResponse.getResult().getMessage().getContent());
			return objectMapper.readValue(jsonText, HamperSolutionDTO.class);
		} catch (JsonProcessingException e) {
			log.error("clova 빨래바구니 세탁 솔루션 json 추출 실패: " + e.getMessage());
			throw new Exception500(ErrorMessage.CLOVA_STUDIO_RESPONSE_PARSING_FAILED);
		}
	}

	/**
	 * 챗봇 스트리밍
	 * HCX-DASH-002 모델 스트리밍 호출
	 */
	public Flux<ServerSentEvent<String>> laundryChatbot(List<String> existingConversation, String newUserMessage) {
		ClovaChatMessageBuilder builder = new ClovaChatMessageBuilder();

		// 시스템 프롬프트 추가
		String systemPrompt = PromptUtils.loadPrompt("prompt/system/chatbot-prompt.md");
		builder.addSystemMessage(systemPrompt);

		// 기존 대화 메세지 생성
		buildExistingMessage(existingConversation, builder);

		// 새 사용자 메시지 추가
		builder.addUserMessage(newUserMessage);

		ClovaChatRequest chatRequest = builder.build();
		try {
			String requestJson = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(chatRequest);
			//log.info("ClovaChatRequest JSON = \n{}", requestJson);
		} catch (Exception e) {
		}

		return client.callChatStreamDASH002(chatRequest);
	}

	private void buildExistingMessage(List<String> existingConversation, ClovaChatMessageBuilder builder) {
		// 이전 대화를 builder로 재구성
		for (String saved : existingConversation) {
			if (saved.startsWith("USER: ")) {
				builder.addUserMessage(saved.substring(6));
			} else if (saved.startsWith("ASSISTANT: ")) {
				builder.addAsistantMessage(saved.substring(11));
			} else if (saved.startsWith("SUMMARY: ")) {
				builder.addAsistantMessage(saved.substring(9));
			}
		}
	}

	public int getTokenCount(String message, String modelName) {
		ClovaChatRequest chatRequest = new ClovaChatMessageBuilder().addUserMessage(message).build();
		ClovaChatTokenizeResponse clovaChatTokenizeResponse = client.callChatTokenize(modelName,
			chatRequest.getMessages());
		return clovaChatTokenizeResponse.getResult().getMessages().getFirst().getContent().getFirst().getCount();
	}

	public SummaryResultDTO summarizeConversation(List<String> existingConversation) {
		ClovaChatMessageBuilder builder = new ClovaChatMessageBuilder();

		// 시스템 프롬프트 추가
		String systemPrompt = PromptUtils.loadPrompt("prompt/system/chatbot-summary-prompt.md");
		builder.addSystemMessage(systemPrompt);

		buildExistingMessage(existingConversation, builder);

		builder.addUserMessage("요약해줘");

		// Response
		ClovaChatResponse clovaChatResponse = client.callChatHCX005(builder.build());

		ClovaChatResponse.Result result = clovaChatResponse.getResult();
		return new SummaryResultDTO(result.getMessage().getContent(), result.getUsage().getCompletionTokens());
	}

	public String weatherDrySolution(String forecast) {
		String systemPrompt = PromptUtils.loadPrompt("prompt/system/weather-dry-solution-prompt.md");

		ClovaChatRequest request = new ClovaChatMessageBuilder()
			.addSystemMessage(systemPrompt)
			.addUserMessage(forecast)
			.build();

		ClovaChatResponse clovaChatResponse = client.callChatHCX005(request);
		return clovaChatResponse.getResult().getMessage().getContent();
	}
}
