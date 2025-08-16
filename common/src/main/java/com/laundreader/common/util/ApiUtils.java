package com.laundreader.common.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

// 공통 응답 DTO
public class ApiUtils {
    public static <T> ApiResult<T> success(T response) {
        return new ApiResult<>(response);
    }

    public static <T> ApiError<T> error(T message) {
        return new ApiError<>(message);
    }

    @Getter
    @AllArgsConstructor
    public static class ApiResult<T> {
        private final T data;
    }

    @Getter
    @AllArgsConstructor
    public static class ApiError<T> {
        private final T error;
    }
}
