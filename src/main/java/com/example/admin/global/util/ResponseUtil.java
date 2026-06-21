package com.example.admin.global.util;

import com.example.admin.global.dto.ApiResponse;

public class ResponseUtil {

    public static <T> ApiResponse<T> success(
            String message,
            T data
    ) {
        return new ApiResponse<>(message, data);
    }

    public static <T> ApiResponse<T> success(
            String message
    ) {
        return new ApiResponse<>(message, null);
    }
}