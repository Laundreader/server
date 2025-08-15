package com.laundreader.external.clova;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laundreader.common.error.ErrorMessage;
import com.laundreader.common.error.exception.Exception500;
import com.laundreader.external.clova.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClovaStudioClient {
    public static final String HCX_005 = "HCX-005";
    public static final String HCX_007 = "HCX-007";

    @Value("${clova.studio.secretKey}")
    private String secretKey;    // Authorization Key

    @Value("${clova.studio.invokeUrl}")
    private String invokeUrl;    // invokeURL from API Gateway

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    // HCX-005 호출 (ClovaChatRequest 전용)
    public ClovaChatResponse callChat(ClovaChatRequest request) {
        ClovaChatResponse response = callModel(HCX_005, request, ClovaChatResponse.class);
        String statusCode = response.getStatus().getCode();
        if (!statusCode.equals("20000") && !statusCode.equals("20400")) {
            log.error(ErrorMessage.CLOVA_STUDIO_REQUEST_FAILED);
            throw new Exception500(ErrorMessage.CLOVA_STUDIO_REQUEST_FAILED);
        }
        return response;
    }

    // HCX-007 호출 (ClovaThinkingRequest 전용)
    public ClovaThinkingResponse callThinking(ClovaThinkingRequest request)  {
        ClovaThinkingResponse response =  callModel(HCX_007, request, ClovaThinkingResponse.class);
        String statusCode = response.getStatus().getCode();
        if (!statusCode.equals("20000") && !statusCode.equals("20400")) {
            log.error(ErrorMessage.CLOVA_STUDIO_REQUEST_FAILED);
            throw new Exception500(ErrorMessage.CLOVA_STUDIO_REQUEST_FAILED);
        }
        return response;
    }

    private <T, R> R callModel(String modelId, T requestBody, Class<R> responseType) {
        String url = invokeUrl + modelId;

        WebClient webClient = webClientBuilder.build();
        String responseBody = webClient.post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + secretKey)
                .header("X-NCP-CLOVASTUDIO-REQUEST-ID", UUID.randomUUID().toString())
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> {
                    log.error("ClovaStudio API request failed with status {}", clientResponse.statusCode());
                    return clientResponse.bodyToMono(String.class)
                            .map(body -> new Exception500(ErrorMessage.CLOVA_STUDIO_REQUEST_FAILED));
                })
                .bodyToMono(String.class)
                .block(); // 동기 호출

        try {
            return objectMapper.readValue(responseBody, responseType);
        } catch (Exception e) {
            throw new Exception500(ErrorMessage.CLOVA_STUDIO_REQUEST_FAILED);
        }
    }
}
