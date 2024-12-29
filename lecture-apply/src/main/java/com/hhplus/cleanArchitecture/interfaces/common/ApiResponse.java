package com.hhplus.cleanArchitecture.interfaces.common;

import lombok.Builder;

public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final String message;
    @Builder
    private ApiResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }
    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }
    public static <T> ApiResponse<T> ok(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .build();
    }
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
    public boolean isSuccess() {
        return success;
    }
    public T getData() {
        return data;
    }
    public String getMessage() {
        return message;
    }
}
