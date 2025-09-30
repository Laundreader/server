package com.laundreader.common.error.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;

import com.laundreader.common.util.ApiUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ValidationException extends RuntimeException {
	private List<ValidationError> validationErrors;

	public ValidationException(List<ValidationError> validationErrors) {
		this.validationErrors = validationErrors;
	}

	public ApiUtils.ApiError<?> body() {
		String message = validationErrors.stream()
			.map(ValidationError::getMessage)
			.collect(Collectors.joining(", "));

		return ApiUtils.error(message);
	}

	public HttpStatus status() {
		return HttpStatus.BAD_REQUEST;
	}

	@Getter
	@AllArgsConstructor
	public static class ValidationError {
		private String field;
		private String message;
	}
}
