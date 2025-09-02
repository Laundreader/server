package com.laundreader.common.error;

import com.laundreader.common.error.exception.*;
import com.laundreader.common.util.ApiUtils;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(Exception400.class)
    public ResponseEntity<?> badRequest(Exception400 e) {
        log.warn(e.getMessage());
        ApiUtils.ApiResult<?> body = ApiUtils.error(e.status(), e.getMessage());
        return new ResponseEntity<>(body, e.status());
    }

    @ExceptionHandler(Exception401.class)
    public ResponseEntity<?> unAuthorized(Exception401 e) {
        log.warn(e.getMessage());
        ApiUtils.ApiResult<?> body = ApiUtils.error(e.status(), e.getMessage());
        return new ResponseEntity<>(body, e.status());
    }

    @ExceptionHandler(Exception403.class)
    public ResponseEntity<?> forbidden(Exception403 e) {
        log.warn(e.getMessage());
        ApiUtils.ApiResult<?> body = ApiUtils.error(e.status(), e.getMessage());
        return new ResponseEntity<>(body, e.status());
    }

    @ExceptionHandler(Exception404.class)
    public ResponseEntity<?> notFound(Exception404 e) {
        log.warn(e.getMessage());
        ApiUtils.ApiResult<?> body = ApiUtils.error(e.status(), e.getMessage());
        return new ResponseEntity<>(body, e.status());
    }

    @ExceptionHandler(Exception500.class)
    public ResponseEntity<?> serverError(Exception500 e) {
        e.printStackTrace();
        log.error(e.getMessage());
        ApiUtils.ApiResult<?> body = ApiUtils.error(e.status(), e.getMessage());
        return new ResponseEntity<>(body, e.status());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> unknownServerError(Exception e) {
        e.printStackTrace();
        log.error(e.getMessage());
        ApiUtils.ApiResult<?> body = ApiUtils.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // @Validated 예외 처리
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> inValidParam(ConstraintViolationException e) {
        log.warn(e.getMessage());
        ApiUtils.ApiResult<?> body = ApiUtils.error(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> inValid(ValidationException e) {
        log.warn(e.getMessage());
        ApiUtils.ApiResult<?> body = ApiUtils.error(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
