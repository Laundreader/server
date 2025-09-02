package com.laundreader.external.clova.request;

public interface ClovaStudioRequestBuilder<T> {
	ClovaStudioRequestBuilder<T> addSystemMessage(String promptFilePath);

	ClovaStudioRequestBuilder<T> addUserMessage(String text);

	ClovaStudioRequestBuilder<T> addUserMessage(String text, String base64Image);

	ClovaStudioRequestBuilder<T> addAsistantMessage(String text);

	T build();
}
