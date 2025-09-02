package com.laundreader.external.clova.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClovaChatTokenizeResponse {
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
		private List<Message> messages;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Message {
		private String role;
		private List<Content> content;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Content {
		private int count;
	}
}
