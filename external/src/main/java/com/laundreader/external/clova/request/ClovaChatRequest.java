package com.laundreader.external.clova.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data
public class ClovaChatRequest {
    // 생성 토큰 후보군을 누적 확률을 기반으로 샘플링 (1.0이면 전체 사용)
    // 확률이 높은 순서대로 누적 합이 P(%) 이상이 될 때까지 후보군을 채우고, 그 안에서 샘플링
    private double topP = 0.80;
    // 생성 토큰 후보군에서 확률이 높은 K개를 후보로 지정하여 샘플링 (높을 수록 다양)
    private int topK = 0;
    // 응답 최대 토큰 수
    private int maxTokens = 4096;
    // 생성 토큰에 대한 다양성 정도(설정값이 높을수록 다양한 문장 생성)
    private double temperature = 0.50;
    // 같은 토큰을 생성하는 것에 대한 패널티 정도(설정값이 높을수록 같은 결괏값을 반복 생성할 확률 감소)
    private double repetitionPenalty = 1.1;
    private List<String> stop = new ArrayList<>();
    private int seed = 0;
    private boolean includeAiFilters = true;

    private List<Message> messages;

    public ClovaChatRequest(List<Message> messages) {
        this.messages = messages;
    }

    @Getter
    @AllArgsConstructor
    public static class Message {
        private String role;
        private List<Content> content;


        @JsonTypeInfo(
                use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXISTING_PROPERTY,
                property = "type",
                visible = true
        )
        @JsonSubTypes({
                @JsonSubTypes.Type(value = TextContent.class, name = "text"),
                @JsonSubTypes.Type(value = ImageUrlContent.class, name = "image_url"),
                @JsonSubTypes.Type(value = DataUriContent.class, name = "data_uri")
        })
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Getter
        public abstract static class Content {
            protected String type;
        }

        @Getter
        @AllArgsConstructor
        public static class TextContent extends Content {
            private String text;

            {
                this.type = "text";
            }
        }

        @Getter
        @AllArgsConstructor
        public static class ImageUrlContent extends Content {
            private ImageUrl imageUrl;

            {
                this.type = "image_url";
            }

            @Getter
            @AllArgsConstructor
            public static class ImageUrl {
                private String url;
            }
        }

        @Getter
        @AllArgsConstructor
        public static class DataUriContent extends Content {
            private DataUri dataUri;

            {
                this.type = "image_url";
            }

            @Getter
            @AllArgsConstructor
            public static class DataUri {
                private String data;
            }
        }
    }
}
