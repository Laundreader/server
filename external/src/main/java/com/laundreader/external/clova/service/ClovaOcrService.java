package com.laundreader.external.clova.service;

import com.laundreader.external.clova.ClovaOcrClient;
import com.laundreader.external.clova.dto.ClovaOcrRequest;
import com.laundreader.external.clova.dto.ClovaOcrResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClovaOcrService {
    private final ClovaOcrClient clovaOcrClient;

    public String extractTextFromImage(String format, String base64PlainText) throws IOException {
        // 1. 이미지 파일을 OCR 클라이언트로 전달하여 텍스트 인식
        ClovaOcrRequest ocrRequest = new ClovaOcrRequest(format, base64PlainText);
        ClovaOcrResponse ocrResponse = clovaOcrClient.send(ocrRequest);

        // 2. OCR 응답에서 각 inferText를 추출하여 하나의 문자열로 합치기
        return ocrResponse.getImages().stream()
                .flatMap(image -> image.getFields().stream())
                .map(ClovaOcrResponse.OcrField::getInferText)
                .collect(Collectors.joining("\n"));
    }
}
