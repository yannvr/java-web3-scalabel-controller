package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Response containing Ethereum balance information")
public class BalanceResponse {

    @NotNull(message = "Address cannot be null")
    @Pattern(regexp = "^0x[a-fA-F0-9]{40}$", message = "Invalid Ethereum address format")
    @Schema(description = "Ethereum address in checksum format", example = "0x742d35Cc6634C0532925a3b844Bc454e4438f44e")
    private String address;

    @NotNull(message = "ETH balance cannot be null")
    @DecimalMin(value = "0.0", message = "ETH balance cannot be negative")
    @Schema(description = "Balance in ETH", example = "1.23456789")
    private BigDecimal balanceEth;

    @NotNull(message = "Wei balance cannot be null")
    @Schema(description = "Balance in Wei (smallest unit)", example = "1234567890000000000")
    private BigInteger balanceWei;

    @Schema(description = "Timestamp of the balance check")
    private LocalDateTime timestamp;

    @Schema(description = "Network identifier", example = "mainnet")
    private String network;
}
