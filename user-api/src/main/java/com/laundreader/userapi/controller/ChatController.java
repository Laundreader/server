package com.laundreader.userapi.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	private final RedisTemplate<String, String> redisTemplate;

	@PostMapping(value = "/stream/{sessionId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<String>> streamChat(
		@PathVariable String sessionId,
		@RequestBody ChatStreamRequest request
	) {
		return chatService.streamChat(sessionId, request.getMessage());
	}

	// @DeleteMapping("/{sessionId}")
	// public ResponseEntity<ApiUtils.ApiResult<Object>> endChat(@PathVariable String sessionId) {
	// 	String key = "chat:" + sessionId;
	// 	Boolean deleted = redisTemplate.delete(key);
	//
	// 	if (Boolean.TRUE.equals(deleted)) {
	// 		return new ResponseEntity<>(ApiUtils.success(null), HttpStatus.OK);
	// 	} else {
	// 		throw new Exception404("세션을 찾을 수 없습니다 sessionId: " + sessionId);
	// 	}
	// }
}