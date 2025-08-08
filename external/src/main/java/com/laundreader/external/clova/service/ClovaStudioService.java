package com.laundreader.external.clova.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laundreader.common.error.ErrorMessage;
import com.laundreader.common.error.exception.Exception500;
import com.laundreader.common.util.JsonExtractor;
import com.laundreader.common.util.TrueFalseExtractor;
import com.laundreader.external.clova.ClovaStudioClient;
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
    private final ObjectMapper objectMapper;


    public String imageAnalysis(String analysisType,String base64Data){
        List<ClovaChatRequest.Message> messageList = new ArrayList<>();

        // SYSTEM
        String prompt = null;
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("prompt/image-analysis-prompt.md");
            prompt = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("프롬프트 변환 실패");
            throw new Exception500(ErrorMessage.INTERNAL_ERROR);
        }

        ClovaChatRequest.Message.TextContent systemContent = new ClovaChatRequest.Message.TextContent(prompt);
        ClovaChatRequest.Message systemMessage = new ClovaChatRequest.Message(Role.SYSTEM.getValue(), List.of(systemContent));
        messageList.add(systemMessage);

        // USER
        // 분석할 이미지 종류 text
        List<ClovaChatRequest.Message.Content> userContents = new ArrayList<>();
        ClovaChatRequest.Message.TextContent userTextContent = new ClovaChatRequest.Message.TextContent(analysisType);
        userContents.add(userTextContent);

        // 분석할 이미지 데이터
        ClovaChatRequest.Message.DataUriContent.DataUri dataUri =  new ClovaChatRequest.Message.DataUriContent.DataUri(base64Data);
        ClovaChatRequest.Message.DataUriContent userDataUriContent = new ClovaChatRequest.Message.DataUriContent(dataUri);
        userContents.add(userDataUriContent);

        ClovaChatRequest.Message userMessage = new ClovaChatRequest.Message(Role.USER.getValue(), userContents);
        messageList.add(userMessage);

        // Request
        ClovaChatResponse clovaChatResponse = client.callChat(new ClovaChatRequest(messageList));
        return TrueFalseExtractor.extractFirstTrueOrFalse(clovaChatResponse.getResult().getMessage().getContent());

    }

    public String labelTextAnalysis(String inputData,String base64Data){
        List<ClovaChatRequest.Message> messageList = new ArrayList<>();

        // SYSTEM
        String prompt = null;
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("prompt/label-text-analysis-prompt.md");
            prompt = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("프롬프트 변환 실패");
            throw new Exception500(ErrorMessage.INTERNAL_ERROR);
        }

        ClovaChatRequest.Message.TextContent systemContent = new ClovaChatRequest.Message.TextContent(prompt);
        ClovaChatRequest.Message systemMessage = new ClovaChatRequest.Message(Role.SYSTEM.getValue(), List.of(systemContent));
        messageList.add(systemMessage);

        // USER
        // 분석할 text
        List<ClovaChatRequest.Message.Content> userContents = new ArrayList<>();
        ClovaChatRequest.Message.TextContent userTextContent = new ClovaChatRequest.Message.TextContent(inputData);
        userContents.add(userTextContent);

        // 분석할 이미지 데이터
        ClovaChatRequest.Message.DataUriContent.DataUri dataUri =  new ClovaChatRequest.Message.DataUriContent.DataUri(base64Data);
        ClovaChatRequest.Message.DataUriContent userDataUriContent = new ClovaChatRequest.Message.DataUriContent(dataUri);
        userContents.add(userDataUriContent);

        ClovaChatRequest.Message userMessage = new ClovaChatRequest.Message(Role.USER.getValue(), userContents);
        messageList.add(userMessage);

        // Request
        ClovaChatResponse clovaChatResponse = client.callChat(new ClovaChatRequest(messageList));

        // json 형식의 답변이 왔는지 검증
        String jsonString = JsonExtractor.extractFirstJsonBlock(clovaChatResponse.getResult().getMessage().getContent());

        try {
            objectMapper.readTree(jsonString);
            return jsonString;
        } catch (Exception e) {
            log.error("clova 응답에서 json 추출 실패");
            throw new Exception500(ErrorMessage.CLOVA_STUDIO_REQUEST_FAILED);
        }
    }

    public String laundrySymbolAnalysis(String inputData,String base64Data) {
        List<ClovaChatRequest.Message> messageList = new ArrayList<>();

        // SYSTEM
        String prompt = null;
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("prompt/laundry-symbol-analysis-prompt.md");
            prompt = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("프롬프트 변환 실패");
            throw new Exception500(ErrorMessage.INTERNAL_ERROR);
        }

        ClovaChatRequest.Message.TextContent systemContent = new ClovaChatRequest.Message.TextContent(prompt);
        ClovaChatRequest.Message systemMessage = new ClovaChatRequest.Message(Role.SYSTEM.getValue(), List.of(systemContent));
        messageList.add(systemMessage);

        // USER
        // 분석할 text
        List<ClovaChatRequest.Message.Content> userContents = new ArrayList<>();
        ClovaChatRequest.Message.TextContent userTextContent = new ClovaChatRequest.Message.TextContent(inputData);
        userContents.add(userTextContent);

        // 분석할 이미지 데이터
        ClovaChatRequest.Message.DataUriContent.DataUri dataUri =  new ClovaChatRequest.Message.DataUriContent.DataUri(base64Data);
        ClovaChatRequest.Message.DataUriContent userDataUriContent = new ClovaChatRequest.Message.DataUriContent(dataUri);
        userContents.add(userDataUriContent);

        ClovaChatRequest.Message userMessage = new ClovaChatRequest.Message(Role.USER.getValue(), userContents);
        messageList.add(userMessage);

        // Request
        ClovaChatResponse clovaChatResponse = client.callChat(new ClovaChatRequest(messageList));

        // json 형식의 답변이 왔는지 검증
        String jsonString = JsonExtractor.extractFirstJsonBlock(clovaChatResponse.getResult().getMessage().getContent());

        try {
            objectMapper.readTree(jsonString);
            return jsonString;
        } catch (Exception e) {
            log.error("clova 응답에서 json 추출 실패");
            throw new Exception500(ErrorMessage.CLOVA_STUDIO_REQUEST_FAILED);
        }
    }

    // 이미지 없는 단일 세탁 솔루션
    public String laundrySolutionSingle(String inputData) {
        List<ClovaChatRequest.Message> messageList = new ArrayList<>();

        // SYSTEM
        String prompt = null;
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("prompt/laundry-solution-single-prompt.md");
            prompt = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("프롬프트 변환 실패");
            throw new Exception500(ErrorMessage.INTERNAL_ERROR);
        }

        ClovaChatRequest.Message.TextContent systemContent = new ClovaChatRequest.Message.TextContent(prompt);
        ClovaChatRequest.Message systemMessage = new ClovaChatRequest.Message(Role.SYSTEM.getValue(), List.of(systemContent));
        messageList.add(systemMessage);

        // USER
        // 분석할 text
        List<ClovaChatRequest.Message.Content> userContents = new ArrayList<>();
        ClovaChatRequest.Message.TextContent userTextContent = new ClovaChatRequest.Message.TextContent(inputData);
        userContents.add(userTextContent);

        ClovaChatRequest.Message userMessage = new ClovaChatRequest.Message(Role.USER.getValue(), userContents);
        messageList.add(userMessage);

        // Request
        ClovaChatResponse clovaChatResponse = client.callChat(new ClovaChatRequest(messageList));

        // json 형식의 답변이 왔는지 검증
        String jsonString = JsonExtractor.extractFirstJsonBlock(clovaChatResponse.getResult().getMessage().getContent());

        try {
            objectMapper.readTree(jsonString);
            return jsonString;
        } catch (Exception e) {
            log.error("clova 응답에서 json 추출 실패");
            throw new Exception500(ErrorMessage.CLOVA_STUDIO_REQUEST_FAILED);
        }
    }

    // 이미지 있는 단일 세탁 솔루션
    public String laundrySolutionSingle(String inputData,String base64Data) {
        List<ClovaChatRequest.Message> messageList = new ArrayList<>();

        // SYSTEM
        String prompt = null;
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("prompt/laundry-solution-single-prompt.md");
            prompt = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("프롬프트 변환 실패");
            throw new Exception500(ErrorMessage.INTERNAL_ERROR);
        }

        ClovaChatRequest.Message.TextContent systemContent = new ClovaChatRequest.Message.TextContent(prompt);
        ClovaChatRequest.Message systemMessage = new ClovaChatRequest.Message(Role.SYSTEM.getValue(), List.of(systemContent));
        messageList.add(systemMessage);

        // USER
        // 분석할 text
        List<ClovaChatRequest.Message.Content> userContents = new ArrayList<>();
        ClovaChatRequest.Message.TextContent userTextContent = new ClovaChatRequest.Message.TextContent(inputData);
        userContents.add(userTextContent);

        // 분석할 이미지 데이터
        ClovaChatRequest.Message.DataUriContent.DataUri dataUri =  new ClovaChatRequest.Message.DataUriContent.DataUri(base64Data);
        ClovaChatRequest.Message.DataUriContent userDataUriContent = new ClovaChatRequest.Message.DataUriContent(dataUri);
        userContents.add(userDataUriContent);

        ClovaChatRequest.Message userMessage = new ClovaChatRequest.Message(Role.USER.getValue(), userContents);
        messageList.add(userMessage);

        // Request
        ClovaChatResponse clovaChatResponse = client.callChat(new ClovaChatRequest(messageList));

        // json 형식의 답변이 왔는지 검증
        String jsonString = JsonExtractor.extractFirstJsonBlock(clovaChatResponse.getResult().getMessage().getContent());

        try {
            objectMapper.readTree(jsonString);
            return jsonString;
        } catch (Exception e) {
            log.error("clova 응답에서 json 추출 실패");
            throw new Exception500(ErrorMessage.CLOVA_STUDIO_REQUEST_FAILED);
        }
    }

    public String laundrySolutionHamper(String inputData) {
        List<ClovaThinkingRequest.Message> messageList = new ArrayList<>();

        // SYSTEM
        String prompt = null;
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("prompt/laundry-solution-hamper-prompt.md");
            prompt = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("프롬프트 변환 실패");
            throw new Exception500(ErrorMessage.INTERNAL_ERROR);
        }

        ClovaThinkingRequest.Message.Content systemContent = new ClovaThinkingRequest.Message.Content(prompt);
        ClovaThinkingRequest.Message systemMessage = new ClovaThinkingRequest.Message(Role.SYSTEM.getValue(), List.of(systemContent));
        messageList.add(systemMessage);

        // USER
        // 분석할 text
        ClovaThinkingRequest.Message.Content userContents = new ClovaThinkingRequest.Message.Content(inputData);
        ClovaThinkingRequest.Message userMessage = new ClovaThinkingRequest.Message(Role.USER.getValue(), List.of(userContents));
        messageList.add(userMessage);

        // Request
        ClovaThinkingResponse clovaThinkingResponse = client.callThinking(new ClovaThinkingRequest(messageList));

        // json 형식의 답변이 왔는지 검증
        String jsonString = JsonExtractor.extractFirstJsonBlock(clovaThinkingResponse.getResult().getMessage().getContent());

        try {
            objectMapper.readTree(jsonString);
            return jsonString;
        } catch (Exception e) {
            log.error("clova 응답에서 json 추출 실패");
            throw new Exception500(ErrorMessage.CLOVA_STUDIO_REQUEST_FAILED);
        }
    }
}
