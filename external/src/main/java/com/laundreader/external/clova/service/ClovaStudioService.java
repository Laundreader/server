package com.laundreader.external.clova.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laundreader.common.error.ErrorMessage;
import com.laundreader.common.error.exception.Exception500;
import com.laundreader.common.util.JsonExtractor;
import com.laundreader.common.util.PromptUtils;
import com.laundreader.common.util.TrueFalseExtractor;
import com.laundreader.external.clova.request.ClovaChatMessageBuilder;
import com.laundreader.external.clova.ClovaStudioClient;
import com.laundreader.external.clova.request.ClovaThinkingMessageBuilder;
import com.laundreader.external.clova.request.ClovaChatRequest;
import com.laundreader.external.clova.response.ClovaChatResponse;
import com.laundreader.external.clova.request.ClovaThinkingRequest;
import com.laundreader.external.clova.response.ClovaThinkingResponse;
import com.laundreader.external.clova.service.response.HamperSolutionResponse;
import com.laundreader.external.clova.service.response.LaundryAnalysisResponse;
import com.laundreader.external.clova.service.response.SingleSolutionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClovaStudioService {
    private final ClovaStudioClient client;
    private final JsonExtractor jsonExtractor;
    private final ObjectMapper objectMapper;

    public boolean imageAnalysis(String analysisType,String base64){
        String systemPrompt = PromptUtils.loadPrompt("prompt/system/image-analysis-prompt.md");

        // Request
        ClovaChatRequest request = new ClovaChatMessageBuilder()
                .addSystemMessage(systemPrompt)
                .addUserMessage(analysisType, base64)
                .build();

        // Response
        ClovaChatResponse clovaChatResponse = client.callChat(request);
        log.info("clova 이미지 검증 Response: " + clovaChatResponse.getResult().getMessage().getContent());
        return TrueFalseExtractor.extractFirstTrueOrFalse(clovaChatResponse.getResult().getMessage().getContent());
    }

    public LaundryAnalysisResponse laundryAnalysis(String ocrText, String labelBase64, String clothesBase64) {
        String systemPrompt = PromptUtils.loadPrompt("prompt/system/laundry-analysis-prompt.md");
        String userFinalTurnPrompt = PromptUtils.loadPrompt("prompt/user/laundry-analysis-prompt.md");

        // Request
        ClovaChatRequest request  = new ClovaChatMessageBuilder()
                .addSystemMessage(systemPrompt)
                .addUserMessage(ocrText, clothesBase64)
                .addUserMessage(userFinalTurnPrompt, labelBase64)
                .build();
//        request.setTemperature(0.3); // 랜덤성을 줄이고 안정적인 응답을 보장
//        request.setTopP(0.8); // 후보 제한을 없애고 가장 확실한 답변만 뽑게 함.
//        request.setTopK(0);

        // Response
        ClovaChatResponse clovaChatResponse = client.callChat(request);
        log.info("clova 세탁물 분석 Response: " + clovaChatResponse.getResult().getMessage().getContent());

        // json 형식의 답변이 왔는지 검증 후 object로 변환하여 return
        try {
            String jsonText = jsonExtractor.extractValidJsonBlock(clovaChatResponse.getResult().getMessage().getContent());
            return objectMapper.readValue(jsonText, LaundryAnalysisResponse.class);
        } catch (JsonProcessingException e) {
            log.error("clova 세탁물 분석 json 추출 실패: " + e.getMessage());
            throw new Exception500(ErrorMessage.CLOVA_STUDIO_RESPONSE_PARSING_FAILED);
        }
    }

    // 단일 세탁 솔루션
    public SingleSolutionResponse laundrySolutionSingle(String inputData) {
        String systemPrompt = PromptUtils.loadPrompt("prompt/system/laundry-solution-single-prompt.md");

        // Request
        ClovaChatRequest request = new ClovaChatMessageBuilder()
                .addSystemMessage(systemPrompt)
                .addUserMessage(inputData)
                .build();

        // Response
        ClovaChatResponse clovaChatResponse = client.callChat(request);
        log.info("clova 단일 세탁 솔루션 Response: " + clovaChatResponse.getResult().getMessage().getContent());

        // json 형식의 답변이 왔는지 검증 후 object로 변환하여 return
        try {
            String jsonText = jsonExtractor.extractValidJsonBlock(clovaChatResponse.getResult().getMessage().getContent());
            return objectMapper.readValue(jsonText, SingleSolutionResponse.class);
        } catch (JsonProcessingException e) {
            log.error("clova 단일 세탁 솔루션 json 추출 실패: " + e.getMessage());
            throw new Exception500(ErrorMessage.CLOVA_STUDIO_RESPONSE_PARSING_FAILED);
        }
    }

    // 빨래 바구니 세탁 솔루션
    public HamperSolutionResponse laundrySolutionHamper(String inputData) {
        String systemPrompt = PromptUtils.loadPrompt("prompt/system/laundry-solution-hamper-prompt.md");

        // Request
        ClovaThinkingRequest clovaThinkingRequest = new ClovaThinkingMessageBuilder()
                .addSystemMessage(systemPrompt)
                .addUserMessage(inputData)
                .build();

        // Response
        ClovaThinkingResponse clovaThinkingResponse = client.callThinking(clovaThinkingRequest);
        log.info("clova 빨래 바구니 세탁 솔루션 Response: " + clovaThinkingResponse.getResult().getMessage().getContent());

        // json 형식의 답변이 왔는지 검증 후 object로 변환하여 return
        try {
            String jsonText = jsonExtractor.extractValidJsonBlock(clovaThinkingResponse.getResult().getMessage().getContent());
            return objectMapper.readValue(jsonText, HamperSolutionResponse.class);
        } catch (JsonProcessingException e) {
            log.error("clova 빨래바구니 세탁 솔루션 json 추출 실패: " + e.getMessage());
            throw new Exception500(ErrorMessage.CLOVA_STUDIO_RESPONSE_PARSING_FAILED);
        }
    }
}
