package com.laundreader.external.clova.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class AssistantSuggestionEventDTO {
	private String type;
	private String message;
	private List<String> suggestions;
}
