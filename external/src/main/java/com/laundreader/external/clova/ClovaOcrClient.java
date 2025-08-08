package com.laundreader.external.clova;

import com.laundreader.common.error.ErrorMessage;
import com.laundreader.common.error.exception.Exception500;
import com.laundreader.external.clova.dto.ClovaOcrRequest;
import com.laundreader.external.clova.dto.ClovaOcrResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClovaOcrClient {
    @Value("${clova.ocr.secretKey}")
    private String secretKey;    // X‑OCR‑SECRET 헤더

    @Value("${clova.ocr.invokeUrl}")
    private String invokeUrl;        // invokeURL from API Gateway

    private final ObjectMapper objectMapper;

    public ClovaOcrResponse send(ClovaOcrRequest request) throws IOException {
        String jsonBody = objectMapper.writeValueAsString(request);
        byte[] body = jsonBody.getBytes(StandardCharsets.UTF_8);

        HttpURLConnection con = (HttpURLConnection) new URL(invokeUrl).openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("X-OCR-SECRET", secretKey);
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            os.write(body);
        }

        int responseCode = con.getResponseCode();
        InputStream is = (responseCode == HttpURLConnection.HTTP_OK)
                ? con.getInputStream()
                : con.getErrorStream();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) sb.append(line);
            String responseBody = sb.toString();

            if (responseCode != HttpURLConnection.HTTP_OK){
                log.error(responseBody);
                throw new Exception500(ErrorMessage.ORC_REQUEST_FAILED);
            }

            return objectMapper.readValue(responseBody, ClovaOcrResponse.class);
        }
    }
}
