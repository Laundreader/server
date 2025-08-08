package com.laundreader.external.clova.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class ClovaChatRequest {
    private double topP = 0.80;
    private int topK = 0;
    private int maxTokens = 4096;
    private double temperature = 0.50;
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
