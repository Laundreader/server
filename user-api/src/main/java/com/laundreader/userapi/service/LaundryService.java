package com.laundreader.userapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laundreader.common.util.Base64Extractor;
import com.laundreader.external.clova.service.ClovaOcrService;
import com.laundreader.external.clova.service.ClovaStudioService;
import com.laundreader.external.clova.service.response.SingleSolutionResponse;
import com.laundreader.userapi.service.dto.HamperDTO;
import com.laundreader.userapi.service.dto.ImageDTO;
import com.laundreader.common.error.ErrorMessage;
import com.laundreader.common.error.exception.Exception500;
import com.laundreader.userapi.service.dto.LaundryDTO;
import com.laundreader.userapi.service.dto.LaundrySymbolDTO;
import com.laundreader.external.clova.service.response.HamperSolutionResponse;
import com.laundreader.external.clova.service.response.LaundryAnalysisResponse;
import com.laundreader.userapi.service.response.laundry.HamperSolutionResponseDTO;
import com.laundreader.userapi.service.response.laundry.LaundryAnalysisResponseDTO;
import com.laundreader.userapi.service.response.laundry.SingleSolutionResponseDTO;
import com.laundreader.userapi.service.type.LaundrySymbolCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class LaundryService {

    private final ClovaOcrService clovaOcrService;
    private final ClovaStudioService clovaStudioService;
    private final ObjectMapper objectMapper;

    public LaundryAnalysisResponseDTO getLaundryAnalysis(ImageDTO labelImage, ImageDTO clothesImage) {
        // OCR 텍스트 추출
        String ocrText;
        try {
            ocrText = clovaOcrService.extractTextFromImage(
                    labelImage.getFormat(),
                    Base64Extractor.extractBase64PlainText(labelImage.getData())
            );
        } catch (IOException e) {
            throw new Exception500(ErrorMessage.ORC_REQUEST_FAILED);
        }

        // OCR 텍스트 + 의류 사진(선택) + 라벨 사진
        LaundryAnalysisResponse clovaResponse = clovaStudioService.laundryAnalysis(
                ocrText,
                labelImage.getData(),
                Optional.ofNullable(clothesImage).map(ImageDTO::getData).orElse(null)
        );

        // 유효하지 않은 심볼 코드 제거
        LaundryAnalysisResponseDTO.LaundrySymbolsDTO filteredSymbols = filterLaundrySymbols(clovaResponse.getLaundrySymbols());

        return new LaundryAnalysisResponseDTO(
                clovaResponse.getMaterials(),
                clovaResponse.getColor(),
                clovaResponse.getType(),
                clovaResponse.getHasPrintOrTrims(),
                clovaResponse.getAdditionalInfo(),
                filteredSymbols
        );
    }

    // 단일 세탁 솔루션
    public SingleSolutionResponseDTO getSingleSolution(LaundryDTO laundry){
        String inputData = null;
        try {
            inputData = objectMapper.writeValueAsString(laundry);
        } catch (JsonProcessingException e) {
            log.error("단독 세탁 솔루션 input String 변환 실패: {}", e.getMessage());
            throw new Exception500(ErrorMessage.INTERNAL_ERROR);
        }

        SingleSolutionResponse clovaResponse = clovaStudioService.laundrySolutionSingle(inputData);

        return new SingleSolutionResponseDTO(
                clovaResponse.getSolutions().stream()
                        .map(s -> new SingleSolutionResponseDTO.SolutionDTO(s.getName(), s.getContents()))
                        .toList()
        );
    }

    public HamperSolutionResponseDTO getHamperSolution(HamperDTO hamper){
        String inputData = null;
        try {
            inputData = objectMapper.writeValueAsString(hamper);
        } catch (JsonProcessingException e) {
            log.error("빨래바구니 세탁 솔루션 input String 변환 실패: {}", e.getMessage());
            throw new Exception500(ErrorMessage.INTERNAL_ERROR);
        }

        HamperSolutionResponse clovaResponse = clovaStudioService.laundrySolutionHamper(inputData);

        return new HamperSolutionResponseDTO(
                clovaResponse.getGroups().stream()
                        .map(g -> new HamperSolutionResponseDTO.groupDTO(
                                g.getId(), g.getName(), g.getSolution(), g.getLaundryIds()
                        ))
                        .toList()
        );
    }


    private LaundryAnalysisResponseDTO.LaundrySymbolsDTO filterLaundrySymbols(LaundryAnalysisResponse.LaundrySymbols symbols){
        if (symbols == null) return null;

        Set<String> validCodes = LaundrySymbolCode.getValidCodes();

        // 외부 DTO → 내부 DTO 복사 및 필터링
        LaundryAnalysisResponseDTO.LaundrySymbolsDTO dto = new LaundryAnalysisResponseDTO.LaundrySymbolsDTO();
        dto.setWaterWashing(filterList(symbols.getWaterWashing(), validCodes));
        dto.setBleaching(filterList(symbols.getBleaching(), validCodes));
        dto.setIroning(filterList(symbols.getIroning(), validCodes));
        dto.setDryCleaning(filterList(symbols.getDryCleaning(), validCodes));
        dto.setWetCleaning(filterList(symbols.getWetCleaning(), validCodes));
        dto.setWringing(filterList(symbols.getWringing(), validCodes));
        dto.setNaturalDrying(filterList(symbols.getNaturalDrying(), validCodes));
        dto.setTumbleDrying(filterList(symbols.getTumbleDrying(), validCodes));

        return dto;
    }

    private List<LaundrySymbolDTO> filterList(
            List<LaundryAnalysisResponse.Symbol> list, Set<String> validCodes) {

        return list == null ? List.of() :
                list.stream()
                        .filter(d -> validCodes.contains(d.getCode()))
                        .map(d -> LaundrySymbolDTO.builder()
                                .code(d.getCode())
                                .description(d.getDescription())
                                .build()
                        )
                        .toList();
    }
}
