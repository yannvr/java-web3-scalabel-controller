package com.example.demo.controller;

import com.example.demo.dto.BalanceResponse;
import com.example.demo.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/ethereum")
@RequiredArgsConstructor
@Validated
@Tag(name = "Ethereum", description = "Ethereum-related operations")
public class EthereumController {

    private final Web3j web3j;

    @GetMapping("/balance/{address}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Get ETH balance",
        description = "Retrieves the current ETH balance for a given Ethereum address"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved balance"),
        @ApiResponse(responseCode = "400", description = "Invalid Ethereum address format"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "429", description = "Too many requests"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getEthBalance(
            @Parameter(description = "Ethereum address to check balance for", example = "0x742d35Cc6634C0532925a3b844Bc454e4438f44e")
            @PathVariable
            @Pattern(regexp = "^0x[a-fA-F0-9]{40}$", message = "Invalid Ethereum address format")
            String address) {
        try {
            // Sanitize the address by converting to lowercase
            String sanitizedAddress = address.toLowerCase();

            // Get balance in Wei
            BigInteger balanceWei = web3j.ethGetBalance(sanitizedAddress, DefaultBlockParameterName.LATEST)
                .send()
                .getBalance();

            // Convert to ETH
            BigDecimal balanceEth = Convert.fromWei(balanceWei.toString(), Convert.Unit.ETHER);

            return ResponseEntity.ok(BalanceResponse.builder()
                .address(sanitizedAddress)
                .balanceEth(balanceEth)
                .balanceWei(balanceWei)
                .timestamp(LocalDateTime.now())
                .network("mainnet")
                .build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ErrorResponse.builder()
                    .error("Failed to fetch ETH balance")
                    .message(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .errorCode("BALANCE_FETCH_ERROR")
                    .details(e.getCause())
                    .build());
        }
    }
}
