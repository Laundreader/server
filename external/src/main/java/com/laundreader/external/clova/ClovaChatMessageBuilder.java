package com.laundreader.external.clova;

import com.laundreader.external.clova.dto.ClovaChatRequest;
import com.laundreader.external.clova.dto.ClovaChatRequest.Message;
import com.laundreader.external.clova.type.Role;

import java.util.ArrayList;
import java.util.List;

public class ClovaChatMessageBuilder implements ClovaStudioRequestBuilder<ClovaChatRequest>  {
    private final List<Message> messages = new ArrayList<>();

    @Override
    public ClovaChatMessageBuilder addSystemMessage(String text) {
        Message.TextContent systemContent = new Message.TextContent(text);
        Message systemMessage = new Message(Role.SYSTEM.getValue(), List.of(systemContent));
        messages.add(systemMessage);
        return this;
    }

    @Override
    public ClovaChatMessageBuilder addUserMessage(String text) {
        Message.TextContent userContent = new Message.TextContent(text);
        Message userMessage = new Message(Role.USER.getValue(), List.of(userContent));
        messages.add(userMessage);
        return this;
    }

    @Override
    public ClovaChatMessageBuilder addUserMessage(String text, String base64Image) {
        List<Message.Content> contents = new ArrayList<>();

        // 텍스트
        contents.add(new Message.TextContent(text));

        // 이미지
        Message.DataUriContent.DataUri dataUri = new Message.DataUriContent.DataUri(base64Image);
        contents.add(new Message.DataUriContent(dataUri));

        Message userMessage = new Message(Role.USER.getValue(), contents);
        messages.add(userMessage);
        return this;
    }

    @Override public ClovaChatRequest build() { return new ClovaChatRequest(messages); }
}
