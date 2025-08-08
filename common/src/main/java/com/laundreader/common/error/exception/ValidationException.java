package com.laundreader.common.error.exception;

import com.laundreader.common.util.ApiUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

public class ValidationException extends RuntimeException {
    @Getter
    @AllArgsConstructor
    public static class ValidationError {
        private String field;
        private String message;
    }

    private List<ValidationError> validationErrors;

    public ValidationException(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public ApiUtils.ApiResult<?> body() {
        return ApiUtils.error(validationErrors);
    }

    public HttpStatus status() {
        return HttpStatus.BAD_REQUEST;
    }
}
