package com.laundreader.external.clova.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClovaChatResponse {
    private Status status;
    private Result result;

    @Getter
    public static class Status {
        private String code;
        private String message;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        private long created;
        private Usage usage;
        private Message message;
        private String finishReason;
        private long seed;
    }

    @Getter
    public static class Usage {
        private int completionTokens;
        private int promptTokens;
        private int totalTokens;
    }

    @Getter
    public static class Message {
        private String role;
        private String content;
    }
}
