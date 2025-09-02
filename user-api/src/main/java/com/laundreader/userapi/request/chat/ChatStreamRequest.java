package com.laundreader.userapi.request.chat;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ChatStreamRequest {
	@NotBlank(message = "빈 문자열 입니다.")
	private String message;
}
