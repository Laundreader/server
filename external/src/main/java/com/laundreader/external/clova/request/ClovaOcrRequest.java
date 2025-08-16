package com.laundreader.external.clova.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClovaOcrRequest {
    private final String version = "V2";
    private final String requestId = UUID.randomUUID().toString();
    private final long timestamp = System.currentTimeMillis();
    private final String lang = "ko, ja, zh-TW";
    private List<Image> images;
    private final boolean enableTableDetection = false;

    public ClovaOcrRequest(String format, String base64PlainText) {
        this.images = List.of(new Image(format, "image-" + this.timestamp, base64PlainText));
    }

    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Image {
        private String format;
        private String name;
        private String data;

        public Image(String format, String name, String base64PlainText) {
            this.format = format;
            this.name = name;
            this.data = base64PlainText;
        }
    }
}
