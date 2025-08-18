package com.laundreader.external.clova.service.response;

import java.util.List;

import lombok.Getter;

@Getter
public class AssistantSuggestionEvent {
	private String type;
	private String message;
	private List<String> suggestions;
}
