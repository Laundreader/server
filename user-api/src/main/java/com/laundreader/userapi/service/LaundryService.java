package com.laundreader.userapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laundreader.common.error.exception.Exception400;
import com.laundreader.common.util.Base64Extractor;
import com.laundreader.external.clova.service.ClovaOcrService;
import com.laundreader.external.clova.service.ClovaStudioService;
import com.laundreader.external.clova.type.ImageAnalysisType;
import com.laundreader.userapi.controller.dto.HamperDTO;
import com.laundreader.userapi.controller.dto.ImageDTO;
import com.laundreader.common.error.ErrorMessage;
import com.laundreader.common.error.exception.Exception500;
import com.laundreader.userapi.controller.dto.LaundryInfoDTO;
import com.laundreader.userapi.service.dto.response.HamperSolutionResponse;
import com.laundreader.userapi.service.dto.response.LabelAnalysisResponse;
import com.laundreader.userapi.service.dto.response.SingleSolutionResponse;
import com.laundreader.userapi.service.dto.LaundrySymbolsDTO;
import com.laundreader.userapi.service.type.LaundrySymbolCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LaundryService {

    private final ClovaOcrService clovaOcrService;
    private final ClovaStudioService clovaStudioService;
    private final ObjectMapper objectMapper;


    public LabelAnalysisResponse getLabelAnalysis(ImageDTO image) {
        LabelAnalysisResponse response;

        // 라벨 이미지가 맞는지 분석
        String result = clovaStudioService.imageAnalysis(ImageAnalysisType.LABEL.getValue(), image.getData());
        if(result.equals("false")){
            throw new Exception400("이미지 입력 오류", ErrorMessage.LAUNDRY_LABEL_NOT_RECOGNIZED);
        }

        // OCR 텍스트 추출
        String ocrText;
        try {
            ocrText = clovaOcrService.extractTextFromImage(image.getFormat(), Base64Extractor.extractBase64PlainText(image.getData()));
        } catch (IOException e) {
            throw new Exception500(ErrorMessage.LAUNDRY_LABEL_NOT_RECOGNIZED);
        }

        // OCR 텍스트 + 세탁 기호 분석 -> 의류 정보 추론
        String labelAnalysis = clovaStudioService.labelAnalysis(ocrText, image.getData(),null);
        try {
            log.info(labelAnalysis);
            response = objectMapper.readValue(labelAnalysis, LabelAnalysisResponse.class);

            LaundrySymbolsDTO laundrySymbols = response.getLaundrySymbols();
            filterLaundrySymbols(laundrySymbols);
            response.setLaundrySymbols(laundrySymbols);

            log.info(response.toString());

        } catch (JsonProcessingException e) {
            log.error("clova 라벨 텍스트 분석 json 추출 실패");
            throw new Exception500(ErrorMessage.CLOVA_STUDIO_REQUEST_FAILED);
        }

        return response;
    }

    // 이미지 없는 단일 세탁 솔루션
    public SingleSolutionResponse getSingleSolution(LaundryInfoDTO laundryInfo){
        SingleSolutionResponse response;

        try {
            String inputData = objectMapper.writeValueAsString(laundryInfo);
            log.info(inputData);
            String solution = clovaStudioService.laundrySolutionSingle(inputData);
            log.info(solution);
            response = objectMapper.readValue(solution, SingleSolutionResponse.class);
        } catch (JsonProcessingException e) {
            log.error("clova 단일 세탁 솔루션 json 추출 실패");
            throw new Exception500(ErrorMessage.CLOVA_STUDIO_REQUEST_FAILED);
        }
        return response;
    }

    // 이미지 있는 단일 세탁 솔루션
    public SingleSolutionResponse getSingleSolution(LaundryInfoDTO laundryInfo, ImageDTO image){
        SingleSolutionResponse response;

        // 의류 이미지가 맞는지 분석
        String result = clovaStudioService.imageAnalysis(ImageAnalysisType.CLOTH.getValue(), image.getData());
        log.info(result);
        if(result.equals("false")){
            throw new Exception400("이미지 입력 오류", ErrorMessage.CLOTHING_NOT_RECOGNIZED);
        }

        // 단일 세탁 솔루션 요청
        try {
            String inputData = objectMapper.writeValueAsString(laundryInfo);
            log.info(inputData);
            String solution = clovaStudioService.laundrySolutionSingle(inputData, image.getData());
            log.info(solution);
            response = objectMapper.readValue(solution, SingleSolutionResponse.class);
        } catch (JsonProcessingException e) {
            log.error("clova 단일 세탁 솔루션 json 추출 실패");
            throw new Exception500(ErrorMessage.CLOVA_STUDIO_REQUEST_FAILED);
        }
        return response;
    }

    public HamperSolutionResponse getHamperSolution(HamperDTO hamper){
        HamperSolutionResponse response;

        try {
            String inputData = objectMapper.writeValueAsString(hamper);
            log.info(inputData);
            String solution = clovaStudioService.laundrySolutionHamper(inputData);
            log.info(solution);
            response = objectMapper.readValue(solution, HamperSolutionResponse.class);
        } catch (JsonProcessingException e) {
            log.error("clova 빨래바구니 세탁 솔루션 json 추출 실패");
            throw new Exception500(ErrorMessage.CLOVA_STUDIO_REQUEST_FAILED);
        }
        return response;
    }


    private void filterLaundrySymbols(LaundrySymbolsDTO laundrySymbols){
        Set<String> validCodes = LaundrySymbolCode.getValidCodes();

        laundrySymbols.setWaterWashing(
                laundrySymbols.getWaterWashing().stream()
                        .filter(detail -> validCodes.contains(detail.getCode()))
                        .collect(Collectors.toList())
        );
        laundrySymbols.setBleaching(
                laundrySymbols.getBleaching().stream()
                        .filter(detail -> validCodes.contains(detail.getCode()))
                        .collect(Collectors.toList())
        );
        laundrySymbols.setIroning(
                laundrySymbols.getIroning().stream()
                        .filter(detail -> validCodes.contains(detail.getCode()))
                        .collect(Collectors.toList())
        );
        laundrySymbols.setDryCleaning(
                laundrySymbols.getDryCleaning().stream()
                        .filter(detail -> validCodes.contains(detail.getCode()))
                        .collect(Collectors.toList())
        );
        laundrySymbols.setWetCleaning(
                laundrySymbols.getWetCleaning().stream()
                        .filter(detail -> validCodes.contains(detail.getCode()))
                        .collect(Collectors.toList())
        );
        laundrySymbols.setWringing(
                laundrySymbols.getWringing().stream()
                        .filter(detail -> validCodes.contains(detail.getCode()))
                        .collect(Collectors.toList())
        );
        laundrySymbols.setNaturalDrying(
                laundrySymbols.getNaturalDrying().stream()
                        .filter(detail -> validCodes.contains(detail.getCode()))
                        .collect(Collectors.toList())
        );
        laundrySymbols.setTumbleDrying(
                laundrySymbols.getTumbleDrying().stream()
                        .filter(detail -> validCodes.contains(detail.getCode()))
                        .collect(Collectors.toList())
        );
    }
}
