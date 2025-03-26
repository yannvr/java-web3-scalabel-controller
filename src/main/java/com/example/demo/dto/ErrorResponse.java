package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Error response containing details about the failure")
public class ErrorResponse {

    @NotNull(message = "Error type cannot be null")
    @NotBlank(message = "Error type cannot be empty")
    @Schema(description = "Type of error that occurred", example = "Failed to fetch ETH balance")
    private String error;

    @NotNull(message = "Error message cannot be null")
    @NotBlank(message = "Error message cannot be empty")
    @Schema(description = "Detailed error message", example = "Connection timeout while fetching balance")
    private String message;

    @Schema(description = "Timestamp when the error occurred")
    private LocalDateTime timestamp;

    @Schema(description = "Error code for programmatic handling", example = "BALANCE_FETCH_ERROR")
    private String errorCode;

    @Schema(description = "Additional error details if available")
    private Object details;
}
