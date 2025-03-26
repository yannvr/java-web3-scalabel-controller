package com.example.demo.exception;

public class Web3jException extends RuntimeException {
    public Web3jException(String message) {
        super(message);
    }

    public Web3jException(String message, Throwable cause) {
        super(message, cause);
    }
}
