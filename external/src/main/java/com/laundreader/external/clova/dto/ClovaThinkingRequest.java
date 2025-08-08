package com.laundreader.external.clova.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
public class ClovaThinkingRequest {
    private double topP = 0.80;
    private int topK = 0;
    private int maxCompletionTokens = 5120;
    private double temperature = 0.50;
    private double repetitionPenalty = 1.1;
    private int seed = 0;
    private boolean includeAiFilters = true;

    private Thinking thinking = new Thinking("low");

    private List<Message> messages;

    public ClovaThinkingRequest(List<Message> messages) {
        this.messages = messages;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Thinking{
        String effort;
    }

    @Getter
    @AllArgsConstructor
    public static class Message {
        private String role;
        private List<Content> content;

        @Getter
        @AllArgsConstructor
        public static class Content {
            private final String type = "text";
            private String text;
        }
    }
}
