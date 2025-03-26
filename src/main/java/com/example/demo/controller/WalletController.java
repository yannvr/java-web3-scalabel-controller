package com.example.demo.controller;

import com.example.demo.model.Wallet;
import com.example.demo.repository.WalletRepository;
import com.example.demo.exception.WalletNotFoundException;
import com.example.demo.dto.WalletOperation;
import com.example.demo.dto.Web3Response;
import com.example.demo.service.WalletMessageProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/wallets")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Wallet Operations", description = "APIs for managing Ethereum wallets")
public class WalletController {

    private final WalletRepository walletRepo;
    private final WalletMessageProducer messageProducer;

    @GetMapping
    public ResponseEntity<List<Wallet>> getAllWallets() {
        return ResponseEntity.ok(walletRepo.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Wallet> getWallet(@PathVariable Long id) {
        return ResponseEntity.ok(walletRepo.findById(id)
            .orElseThrow(() -> new WalletNotFoundException("Wallet not found with id: " + id)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Wallet> createWallet(@RequestBody Wallet wallet) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(walletRepo.save(wallet));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Wallet> updateWallet(@PathVariable Long id, @RequestBody Wallet wallet) {
        if (!walletRepo.existsById(id)) {
            throw new WalletNotFoundException("Wallet not found with id: " + id);
        }
        wallet.setId(id);
        return ResponseEntity.ok(walletRepo.save(wallet));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteWallet(@PathVariable Long id) {
        if (!walletRepo.existsById(id)) {
            throw new WalletNotFoundException("Wallet not found with id: " + id);
        }
        walletRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Create a new wallet")
    @PostMapping("/create")
    public CompletableFuture<ResponseEntity<Web3Response<Map<String, Object>>>> createWallet() {
        WalletOperation operation = new WalletOperation();
        operation.setId(UUID.randomUUID().toString());
        operation.setType(WalletOperation.OperationType.CREATE_WALLET);

        return messageProducer.sendOperation(operation)
            .thenApply(v -> ResponseEntity.ok(operation.getResult()))
            .exceptionally(throwable -> ResponseEntity.internalServerError()
                .body(Web3Response.error(throwable.getMessage())));
    }

    @Operation(summary = "Get wallet balance")
    @GetMapping("/balance/{address}")
    public CompletableFuture<ResponseEntity<Web3Response<Map<String, Object>>>> getBalance(
            @Parameter(description = "Ethereum address") @PathVariable String address) {
        WalletOperation operation = new WalletOperation();
        operation.setId(UUID.randomUUID().toString());
        operation.setType(WalletOperation.OperationType.GET_BALANCE);
        operation.setAddress(address);

        return messageProducer.sendOperation(operation)
            .thenApply(v -> ResponseEntity.ok(operation.getResult()))
            .exceptionally(throwable -> ResponseEntity.internalServerError()
                .body(Web3Response.error(throwable.getMessage())));
    }

    @Operation(summary = "Send ETH to an address")
    @PostMapping("/send")
    public CompletableFuture<ResponseEntity<Web3Response<Map<String, Object>>>> sendEth(
            @Parameter(description = "From address") @RequestParam String from,
            @Parameter(description = "To address") @RequestParam String to,
            @Parameter(description = "Amount in ETH") @RequestParam BigDecimal amount) {
        WalletOperation operation = new WalletOperation();
        operation.setId(UUID.randomUUID().toString());
        operation.setType(WalletOperation.OperationType.SEND_ETH);
        operation.setAddress(from);
        operation.setToAddress(to);
        operation.setAmount(amount);

        return messageProducer.sendOperation(operation)
            .thenApply(v -> ResponseEntity.ok(operation.getResult()))
            .exceptionally(throwable -> ResponseEntity.internalServerError()
                .body(Web3Response.error(throwable.getMessage())));
    }

    @Operation(summary = "Get transaction history")
    @GetMapping("/transactions/{address}")
    public CompletableFuture<ResponseEntity<Web3Response<Map<String, Object>>>> getTransactionHistory(
            @Parameter(description = "Ethereum address") @PathVariable String address) {
        WalletOperation operation = new WalletOperation();
        operation.setId(UUID.randomUUID().toString());
        operation.setType(WalletOperation.OperationType.GET_TRANSACTION_HISTORY);
        operation.setAddress(address);

        return messageProducer.sendOperation(operation)
            .thenApply(v -> ResponseEntity.ok(operation.getResult()))
            .exceptionally(throwable -> ResponseEntity.internalServerError()
                .body(Web3Response.error(throwable.getMessage())));
    }
}
