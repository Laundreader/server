package com.laundreader.external.clova.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true) //Java 객체에는 없는 필드를 무시
public class ClovaOcrResponse {
    private List<OcrImage> images;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OcrImage {
        private List<OcrField> fields;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OcrField {
        private String inferText;

        @JsonProperty("inferText")
        public String getInferText() {
            return inferText;
        }
    }
}

