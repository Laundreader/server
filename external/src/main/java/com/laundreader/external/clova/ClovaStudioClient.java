package com.laundreader.external.clova;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laundreader.common.error.ErrorMessage;
import com.laundreader.common.error.exception.Exception500;
import com.laundreader.external.clova.request.ClovaChatRequest;
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
public class ClovaStudioClient {
	public static final String HCX_DASH_002 = "HCX-DASH-002";
	public static final String HCX_005 = "HCX-005";
	public static final String HCX_007 = "HCX-007";

	private final WebClient.Builder webClientBuilder;
	private final ObjectMapper objectMapper;

	@Value("${clova.studio.secretKey}")
	private String secretKey;    // Authorization Key
	@Value("${clova.studio.chatCompletionsUrl}")
	private String chatCompletionsUrl;    // Chat-Completions 요청 URL
	@Value("${clova.studio.chatTokenizeUrl}")
	private String chatTokenizeUrl;    // Chat-Tokenize 요청 URL

	// HCX-005 호출 (ClovaChatRequest 전용)
	public ClovaChatResponse callChatHCX005(ClovaChatRequest request) {
		ClovaChatResponse response = callModel(chatCompletionsUrl + HCX_005, request, ClovaChatResponse.class);

		String statusCode = response.getStatus().getCode();
		if (!statusCode.equals("20000") && !statusCode.equals("20400")) {
			String statusMessage = response.getStatus().getMessage();

			log.error("{}: code={}, message={}",
				ErrorMessage.CLOVA_STUDIO_REQUEST_FAILED,
				statusCode,
				statusMessage
			);
			throw new Exception500(
				String.format("%s: code=%s, message=%s",
					ErrorMessage.CLOVA_STUDIO_REQUEST_FAILED,
					statusCode,
					statusMessage
				)
			);
		}
		return response;
	}

	// HCX-007 호출 (ClovaThinkingRequest 전용)
	public ClovaThinkingResponse callThinkingHCX007(ClovaThinkingRequest request) {
		ClovaThinkingResponse response = callModel(chatCompletionsUrl + HCX_007, request, ClovaThinkingResponse.class);
		String statusCode = response.getStatus().getCode();
		if (!statusCode.equals("20000") && !statusCode.equals("20400")) {
			String statusMessage = response.getStatus().getMessage();

			log.error("{}: code={}, message={}",
				ErrorMessage.CLOVA_STUDIO_REQUEST_FAILED,
				statusCode,
				statusMessage
			);
			throw new Exception500(
				String.format("%s: code=%s, message=%s",
					ErrorMessage.CLOVA_STUDIO_REQUEST_FAILED,
					statusCode,
					statusMessage
				)
			);
		}
		return response;
	}

	// HCX-DASH-002 호출 (챗봇 스트리밍)
	public Flux<ServerSentEvent<String>> callChatStreamDASH002(ClovaChatRequest request) {
		return callModelStream(chatCompletionsUrl + HCX_DASH_002, request);
	}

	public ClovaChatTokenizeResponse callChatTokenize(String modelName, List<ClovaChatRequest.Message> messages) {
		return callModel(chatTokenizeUrl + modelName, Map.of("messages", messages), ClovaChatTokenizeResponse.class);
	}

	private <T, R> R callModel(String url, T requestBody, Class<R> responseType) {
		WebClient webClient = webClientBuilder.build();
		String responseBody = webClient.post()
			.uri(url)
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + secretKey)
			.header("X-NCP-CLOVASTUDIO-REQUEST-ID", UUID.randomUUID().toString())
			.bodyValue(requestBody)
			.retrieve()
			.bodyToMono(String.class)
			.block(); // 동기 호출
		try {
			return objectMapper.readValue(responseBody, responseType);
		} catch (Exception e) {
			throw new Exception500(ErrorMessage.CLOVA_STUDIO_REQUEST_FAILED);
		}
	}

	public <T> Flux<ServerSentEvent<String>> callModelStream(String url, T requestBody) {
		WebClient webClient = webClientBuilder.build();

		return webClient.post()
			.uri(url)
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + secretKey)
			.header("X-NCP-CLOVASTUDIO-REQUEST-ID", UUID.randomUUID().toString())
			.bodyValue(requestBody)
			.accept(MediaType.TEXT_EVENT_STREAM) // SSE 형식
			.retrieve()
			.bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {
			});
	}
}
