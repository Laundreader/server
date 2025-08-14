package com.laundreader.common.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

// 공통 응답 DTO
public class ApiUtils {
    public static <T> ApiResult<T> success(HttpStatus status, T response) {
        return new ApiResult<>(status.value(), response, null);
    }

    public static <T> ApiResult<T> error(HttpStatus status, T message) {
        return new ApiResult<>( status.value(), null , new ApiError(message));
    }

    @Getter
    @AllArgsConstructor
    public static class ApiResult<T> {
        private final int status;
        private final T data;
        private final ApiError error;
    }

    @Getter
    @AllArgsConstructor
    public static class ApiError<T> {
        private final T message;
    }
}
