package com.example.demo.controller;

import com.example.demo.dto.Web3Operation;
import com.example.demo.dto.Web3Response;
import com.example.demo.service.IWeb3jService;
import com.example.demo.service.Web3MessageProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/web3")
@RequiredArgsConstructor
@Tag(name = "Web3 Operations", description = "APIs for interacting with Ethereum blockchain")
public class Web3jController {

    private final Web3MessageProducer messageProducer;
    private final IWeb3jService web3jService;

    @Operation(summary = "Get ETH balance")
    @GetMapping("/balance/{address}")
    public CompletableFuture<ResponseEntity<Web3Response<Map<String, Object>>>> getEthBalance(
            @Parameter(description = "Ethereum address") @PathVariable String address) {
        Web3Operation operation = new Web3Operation();
        operation.setId(UUID.randomUUID().toString());
        operation.setType(Web3Operation.OperationType.GET_BALANCE);
        operation.setAddress(address);

        return messageProducer.sendOperation(operation)
            .thenApply(v -> ResponseEntity.ok(operation.getResult()))
            .exceptionally(throwable -> ResponseEntity.internalServerError()
                .body(Web3Response.error(throwable.getMessage())));
    }

    @Operation(summary = "Get transaction count")
    @GetMapping("/transaction-count/{address}")
    public CompletableFuture<ResponseEntity<Web3Response<Map<String, Object>>>> getTransactionCount(
            @Parameter(description = "Ethereum address") @PathVariable String address) {
        Web3Operation operation = new Web3Operation();
        operation.setId(UUID.randomUUID().toString());
        operation.setType(Web3Operation.OperationType.GET_TRANSACTION_COUNT);
        operation.setAddress(address);

        return messageProducer.sendOperation(operation)
            .thenApply(v -> ResponseEntity.ok(operation.getResult()))
            .exceptionally(throwable -> ResponseEntity.internalServerError()
                .body(Web3Response.error(throwable.getMessage())));
    }

    @Operation(summary = "Get gas price")
    @GetMapping("/gas-price")
    public CompletableFuture<ResponseEntity<Web3Response<Map<String, Object>>>> getGasPrice() {
        Web3Operation operation = new Web3Operation();
        operation.setId(UUID.randomUUID().toString());
        operation.setType(Web3Operation.OperationType.GET_GAS_PRICE);

        return messageProducer.sendOperation(operation)
            .thenApply(v -> ResponseEntity.ok(operation.getResult()))
            .exceptionally(throwable -> ResponseEntity.internalServerError()
                .body(Web3Response.error(throwable.getMessage())));
    }

    @Operation(summary = "Get latest block number")
    @GetMapping("/latest-block")
    public CompletableFuture<ResponseEntity<Web3Response<Map<String, Object>>>> getLatestBlockNumber() {
        Web3Operation operation = new Web3Operation();
        operation.setId(UUID.randomUUID().toString());
        operation.setType(Web3Operation.OperationType.GET_LATEST_BLOCK);

        return messageProducer.sendOperation(operation)
            .thenApply(v -> ResponseEntity.ok(operation.getResult()))
            .exceptionally(throwable -> ResponseEntity.internalServerError()
                .body(Web3Response.error(throwable.getMessage())));
    }

    @Operation(summary = "Get block by number")
    @GetMapping("/block/{number}")
    public CompletableFuture<ResponseEntity<Web3Response<Map<String, Object>>>> getBlockByNumber(
            @Parameter(description = "Block number") @PathVariable BigInteger number) {
        Web3Operation operation = new Web3Operation();
        operation.setId(UUID.randomUUID().toString());
        operation.setType(Web3Operation.OperationType.GET_BLOCK);
        operation.setAddress(number.toString());

        return messageProducer.sendOperation(operation)
            .thenApply(v -> ResponseEntity.ok(operation.getResult()))
            .exceptionally(throwable -> ResponseEntity.internalServerError()
                .body(Web3Response.error(throwable.getMessage())));
    }

    @Operation(summary = "Get transaction by hash")
    @GetMapping("/transaction/{hash}")
    public CompletableFuture<ResponseEntity<Web3Response<Map<String, Object>>>> getTransactionByHash(
            @Parameter(description = "Transaction hash") @PathVariable String hash) {
        Web3Operation operation = new Web3Operation();
        operation.setId(UUID.randomUUID().toString());
        operation.setType(Web3Operation.OperationType.GET_TRANSACTION);
        operation.setAddress(hash);

        return messageProducer.sendOperation(operation)
            .thenApply(v -> ResponseEntity.ok(operation.getResult()))
            .exceptionally(throwable -> ResponseEntity.internalServerError()
                .body(Web3Response.error(throwable.getMessage())));
    }

    @Operation(summary = "Get transaction receipt")
    @GetMapping("/transaction-receipt/{hash}")
    public CompletableFuture<ResponseEntity<Web3Response<Map<String, Object>>>> getTransactionReceipt(
            @Parameter(description = "Transaction hash") @PathVariable String hash) {
        Web3Operation operation = new Web3Operation();
        operation.setId(UUID.randomUUID().toString());
        operation.setType(Web3Operation.OperationType.GET_TRANSACTION_RECEIPT);
        operation.setAddress(hash);

        return messageProducer.sendOperation(operation)
            .thenApply(v -> ResponseEntity.ok(operation.getResult()))
            .exceptionally(throwable -> ResponseEntity.internalServerError()
                .body(Web3Response.error(throwable.getMessage())));
    }

    @Operation(summary = "Estimate gas for transaction")
    @PostMapping("/estimate-gas")
    public CompletableFuture<ResponseEntity<Web3Response<Map<String, Object>>>> estimateGas(
            @Parameter(description = "From address") @RequestParam String from,
            @Parameter(description = "To address") @RequestParam String to,
            @Parameter(description = "Value in ETH") @RequestParam BigDecimal value) {
        Web3Operation operation = new Web3Operation();
        operation.setId(UUID.randomUUID().toString());
        operation.setType(Web3Operation.OperationType.ESTIMATE_GAS);
        operation.setAddress(from + "," + to + "," + value);

        return messageProducer.sendOperation(operation)
            .thenApply(v -> ResponseEntity.ok(operation.getResult()))
            .exceptionally(throwable -> ResponseEntity.internalServerError()
                .body(Web3Response.error(throwable.getMessage())));
    }

    @Operation(summary = "Check if node is syncing")
    @GetMapping("/syncing")
    public CompletableFuture<ResponseEntity<Web3Response<Map<String, Object>>>> isNodeSyncing() {
        Web3Operation operation = new Web3Operation();
        operation.setId(UUID.randomUUID().toString());
        operation.setType(Web3Operation.OperationType.GET_NETWORK_INFO);

        return messageProducer.sendOperation(operation)
            .thenApply(v -> ResponseEntity.ok(operation.getResult()))
            .exceptionally(throwable -> ResponseEntity.internalServerError()
                .body(Web3Response.error(throwable.getMessage())));
    }

    @Operation(summary = "Get network ID")
    @GetMapping("/network-id")
    public CompletableFuture<ResponseEntity<Web3Response<Map<String, Object>>>> getNetworkId() {
        Web3Operation operation = new Web3Operation();
        operation.setId(UUID.randomUUID().toString());
        operation.setType(Web3Operation.OperationType.GET_NETWORK_INFO);

        return messageProducer.sendOperation(operation)
            .thenApply(v -> ResponseEntity.ok(operation.getResult()))
            .exceptionally(throwable -> ResponseEntity.internalServerError()
                .body(Web3Response.error(throwable.getMessage())));
    }
}
