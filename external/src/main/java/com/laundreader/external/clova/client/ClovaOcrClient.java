package com.laundreader.external.clova.client;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laundreader.common.error.ErrorMessage;
import com.laundreader.common.error.exception.Exception500;
import com.laundreader.external.clova.request.ClovaOcrRequest;
import com.laundreader.external.clova.response.ClovaOcrResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClovaOcrClient {
	private final WebClient.Builder webClientBuilder;
	private final ObjectMapper objectMapper;
	@Value("${clova.ocr.secretKey}")
	private String secretKey;    // X‑OCR‑SECRET 헤더
	@Value("${clova.ocr.invokeUrl}")
	private String invokeUrl;        // invokeURL from API Gateway

	public ClovaOcrResponse send(ClovaOcrRequest request) throws IOException {
		WebClient webClient = webClientBuilder.baseUrl(invokeUrl).build();

		try {
			String responseBody = webClient.post()
				.contentType(MediaType.APPLICATION_JSON)
				.header("X-OCR-SECRET", secretKey)
				.bodyValue(request)
				.retrieve()
				.bodyToMono(String.class) // 우선 문자열로 받고
				.block(); // 동기 호출

			return objectMapper.readValue(responseBody, ClovaOcrResponse.class);

		} catch (WebClientResponseException e) {
			log.error("OCR 요청 실패: {}", e.getResponseBodyAsString(), e);
			throw new Exception500(ErrorMessage.ORC_REQUEST_FAILED);
		} catch (Exception e) {
			log.error("OCR 처리 중 오류 발생", e);
			throw new Exception500(ErrorMessage.ORC_REQUEST_FAILED);
		}
	}
}
