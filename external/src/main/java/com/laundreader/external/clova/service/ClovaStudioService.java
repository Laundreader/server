package com.laundreader.external.clova.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laundreader.common.error.ErrorMessage;
import com.laundreader.common.error.exception.Exception500;
import com.laundreader.common.util.JsonExtractor;
import com.laundreader.common.util.PromptUtils;
import com.laundreader.common.util.TrueFalseExtractor;
import com.laundreader.external.clova.ClovaChatMessageBuilder;
import com.laundreader.external.clova.ClovaStudioClient;
import com.laundreader.external.clova.ClovaThinkingMessageBuilder;
import com.laundreader.external.clova.dto.ClovaChatRequest;
import com.laundreader.external.clova.dto.ClovaChatResponse;
import com.laundreader.external.clova.dto.ClovaThinkingRequest;
import com.laundreader.external.clova.dto.ClovaThinkingResponse;
import com.laundreader.external.clova.type.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClovaStudioService {
    private final ClovaStudioClient client;
    private final JsonExtractor jsonExtractor;

    public boolean imageAnalysis(String analysisType,String base64){
        // Request
        ClovaChatRequest request = new ClovaChatMessageBuilder()
                .addSystemMessage("prompt/system/image-analysis-prompt.md")
                .addUserMessage(analysisType, base64)
                .build();

        // Response
        ClovaChatResponse clovaChatResponse = client.callChat(request);
        return TrueFalseExtractor.extractFirstTrueOrFalse(clovaChatResponse.getResult().getMessage().getContent());
    }

    public String labelAnalysis(String ocrText, String labelBase64, String clothesBase64) {
        String systemPrompt = PromptUtils.loadPrompt("prompt/system/label-analysis-prompt.md");
        String userFinalTurnPrompt = ocrText + PromptUtils.loadPrompt("prompt/user/label-analysis-prompt.md");

        // Request
        ClovaChatMessageBuilder builder = new ClovaChatMessageBuilder()
                .addSystemMessage(systemPrompt)
                .addUserMessage("", labelBase64);

        if (clothesBase64 != null) {
            builder.addUserMessage(userFinalTurnPrompt, clothesBase64);
        } else {
            builder.addUserMessage(userFinalTurnPrompt);
        }

        ClovaChatRequest request = builder.build();

        // Response
        ClovaChatResponse clovaChatResponse = client.callChat(request);

        // json 형식의 답변이 왔는지 검증 후 return
        try {
            return jsonExtractor.extractValidJsonBlock(clovaChatResponse.getResult().getMessage().getContent());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception500(ErrorMessage.CLOVA_STUDIO_REQUEST_FAILED + ": e.getMessage()");
        }
    }

    // 이미지 없는 단일 세탁 솔루션
    public String laundrySolutionSingle(String inputData) {
        String systemPrompt = PromptUtils.loadPrompt("prompt/system/laundry-solution-single-prompt.md");

        // Request
        ClovaChatRequest request = new ClovaChatMessageBuilder()
                .addSystemMessage(systemPrompt)
                .addUserMessage(inputData)
                .build();

        // Response
        ClovaChatResponse clovaChatResponse = client.callChat(request);

        // json 형식의 답변이 왔는지 검증 후 return
        try {
            return jsonExtractor.extractValidJsonBlock(clovaChatResponse.getResult().getMessage().getContent());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception500(ErrorMessage.CLOVA_STUDIO_REQUEST_FAILED + ": e.getMessage()");
        }
    }

    // 이미지 있는 단일 세탁 솔루션
    public String laundrySolutionSingle(String inputData,String base64) {
        String systemPrompt = PromptUtils.loadPrompt("prompt/system/laundry-solution-single-prompt.md");

        // Request
        ClovaChatRequest request = new ClovaChatMessageBuilder()
                .addSystemMessage(systemPrompt)
                .addUserMessage(inputData, base64)
                .build();

        // Response
        ClovaChatResponse clovaChatResponse = client.callChat(request);

        // json 형식의 답변이 왔는지 검증 후 return
        try {
            return jsonExtractor.extractValidJsonBlock(clovaChatResponse.getResult().getMessage().getContent());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception500(ErrorMessage.CLOVA_STUDIO_REQUEST_FAILED + ": e.getMessage()");
        }
    }

    public String laundrySolutionHamper(String inputData) {
        String systemPrompt = PromptUtils.loadPrompt("prompt/system/laundry-solution-hamper-prompt.md");

        // Request
        ClovaThinkingRequest clovaThinkingRequest = new ClovaThinkingMessageBuilder()
                .addSystemMessage(systemPrompt)
                .addUserMessage(inputData)
                .build();

        // Response
        ClovaThinkingResponse clovaThinkingResponse = client.callThinking(clovaThinkingRequest);

        // json 형식의 답변이 왔는지 검증 후 return
        try {
            return jsonExtractor.extractValidJsonBlock(clovaThinkingResponse.getResult().getMessage().getContent());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception500(ErrorMessage.CLOVA_STUDIO_REQUEST_FAILED + ": e.getMessage()");
        }
    }
}
