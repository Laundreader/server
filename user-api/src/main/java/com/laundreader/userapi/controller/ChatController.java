package com.laundreader.userapi.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laundreader.common.util.ApiUtils;
import com.laundreader.common.util.SessionIdGenerator;
import com.laundreader.userapi.controller.request.chat.ChatStreamRequest;
import com.laundreader.userapi.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/user-api/chat/")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ChatController {
	private final ChatService chatService;

	@PostMapping("/stream")
	public ResponseEntity<ApiUtils.ApiResult<Object>> getSession() {
		String sessionId = SessionIdGenerator.generateSessionId(16);
		return new ResponseEntity<>(ApiUtils.success(Map.of("sessionId", sessionId)), HttpStatus.OK);
	}

	@PostMapping(value = "/stream/{sessionId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<String>> streamChat(
		@PathVariable String sessionId,
		@RequestBody ChatStreamRequest request
	) {
		return chatService.streamChat(sessionId, request.getMessage());
	}

	@DeleteMapping("/{sessionId}")
	public ResponseEntity<ApiUtils.ApiResult<Object>> endChat(@PathVariable String sessionId) {
		chatService.deleteSesstion(sessionId);
		return new ResponseEntity<>(ApiUtils.success(null), HttpStatus.OK);
	}
}