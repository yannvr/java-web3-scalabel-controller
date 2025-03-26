package com.example.demo.exception;

import com.example.demo.dto.Web3Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class Web3jExceptionHandler {

    @ExceptionHandler(Web3jException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Web3Response<Void>> handleWeb3jException(Web3jException ex, WebRequest request) {
        log.error("Web3j error: {}", ex.getMessage(), ex);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Web3Response.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Web3Response<Void>> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Web3Response.error("An unexpected error occurred"));
    }
}
