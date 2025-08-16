package com.laundreader.external.clova.request;

import com.laundreader.external.clova.request.ClovaThinkingRequest.Message;
import com.laundreader.external.clova.type.Role;

import java.util.ArrayList;
import java.util.List;

public class ClovaThinkingMessageBuilder implements ClovaStudioRequestBuilder<ClovaThinkingRequest>  {
    private final List<Message> messages = new ArrayList<>();

    @Override
    public ClovaThinkingMessageBuilder addSystemMessage(String text) {
        Message.Content systemContent = new Message.Content(text);
        Message systemMessage = new Message(Role.SYSTEM.getValue(), List.of(systemContent));
        messages.add(systemMessage);
        return this;
    }

    @Override
    public ClovaThinkingMessageBuilder addUserMessage(String text) {
        Message.Content userContent = new Message.Content(text);
        Message userMessage = new Message(Role.USER.getValue(), List.of(userContent));
        messages.add(userMessage);
        return this;
    }

    @Override
    public ClovaThinkingMessageBuilder addUserMessage(String text, String base64Image) {
        // Thinking에서는 이미지 지원 안 함 → 무시
        return this;
    }

    @Override public ClovaThinkingRequest build() { return new ClovaThinkingRequest(messages); }
}
