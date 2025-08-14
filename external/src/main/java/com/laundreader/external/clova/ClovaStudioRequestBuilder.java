package com.laundreader.external.clova;

public interface ClovaStudioRequestBuilder<T> {
    ClovaStudioRequestBuilder<T> addSystemMessage(String promptFilePath);
    ClovaStudioRequestBuilder<T> addUserMessage(String text);
    ClovaStudioRequestBuilder<T> addUserMessage(String text, String base64Image);
    T build();
}
