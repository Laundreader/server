package com.laundreader.common.error;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.laundreader.common.error.exception.Exception400;
import com.laundreader.common.error.exception.Exception401;
import com.laundreader.common.error.exception.Exception403;
import com.laundreader.common.error.exception.Exception404;
import com.laundreader.common.error.exception.Exception500;
import com.laundreader.common.error.exception.ValidationException;
import com.laundreader.common.util.ApiUtils;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class MyExceptionHandler {

	@ExceptionHandler(Exception400.class)
	public ResponseEntity<?> badRequest(Exception400 e) {
		log.warn(e.getMessage());
		ApiUtils.ApiError<?> body = ApiUtils.error(e.getMessage());
		return new ResponseEntity<>(body, e.status());
	}

	@ExceptionHandler(Exception401.class)
	public ResponseEntity<?> unAuthorized(Exception401 e) {
		log.warn(e.getMessage());
		ApiUtils.ApiError<?> body = ApiUtils.error(e.getMessage());
		return new ResponseEntity<>(body, e.status());
	}

	@ExceptionHandler(Exception403.class)
	public ResponseEntity<?> forbidden(Exception403 e) {
		log.warn(e.getMessage());
		ApiUtils.ApiError<?> body = ApiUtils.error(e.getMessage());
		return new ResponseEntity<>(body, e.status());
	}

	@ExceptionHandler(Exception404.class)
	public ResponseEntity<?> notFound(Exception404 e) {
		log.warn(e.getMessage());
		ApiUtils.ApiError<?> body = ApiUtils.error(e.getMessage());
		return new ResponseEntity<>(body, e.status());
	}

	@ExceptionHandler(Exception500.class)
	public ResponseEntity<?> serverError(Exception500 e) {
		e.printStackTrace();
		log.error(e.getMessage());
		ApiUtils.ApiError<?> body = ApiUtils.error(e.getMessage());
		return new ResponseEntity<>(body, e.status());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> unknownServerError(Exception e) {
		e.printStackTrace();
		log.error(e.getMessage());
		ApiUtils.ApiError<?> body = ApiUtils.error(e.getMessage());
		return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// @Validated 예외 처리
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<?> inValidParam(ConstraintViolationException e) {
		// 모든 메시지를 콤마로 합침
		String message = e.getConstraintViolations().stream()
			.map(cv -> cv.getMessage())
			.collect(Collectors.joining(", "));

		ApiUtils.ApiError<?> body = ApiUtils.error(message);
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<?> inValid(ValidationException e) {
		ApiUtils.ApiError<?> body = e.body();
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
		String message = e.getBindingResult().getFieldErrors().stream()
			.map(error -> error.getDefaultMessage())
			.collect(Collectors.joining(", "));

		ApiUtils.ApiError<?> body = ApiUtils.error(message);
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
}
