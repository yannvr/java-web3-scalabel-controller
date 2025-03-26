package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web3Response<T> {
    private boolean success;
    private String message;
    private T data;
    private String error;

    public static <T> Web3Response<T> success(T data) {
        return Web3Response.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> Web3Response<T> error(String message) {
        return Web3Response.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
