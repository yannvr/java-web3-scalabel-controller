package com.example.demo.controller;

import com.example.demo.dto.Web3Response;
import com.example.demo.service.IWeb3jService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/web3")
@RequiredArgsConstructor
@Tag(name = "Web3j Operations", description = "APIs for interacting with Ethereum blockchain")
public class Web3jController {

    private final IWeb3jService web3jService;

    @Operation(summary = "Get ETH balance for an address")
    @GetMapping("/balance/{address}")
    public ResponseEntity<Web3Response<Map<String, Object>>> getEthBalance(
            @Parameter(description = "Ethereum address") @PathVariable String address) {
        BigDecimal balance = web3jService.getEthBalance(address);
        return ResponseEntity.ok(Web3Response.success(Map.of(
            "address", address,
            "balance", balance
        )));
    }

    @Operation(summary = "Get transaction count for an address")
    @GetMapping("/transaction-count/{address}")
    public ResponseEntity<Web3Response<Map<String, Object>>> getTransactionCount(
            @Parameter(description = "Ethereum address") @PathVariable String address) {
        BigInteger count = web3jService.getTransactionCount(address);
        return ResponseEntity.ok(Web3Response.success(Map.of(
            "address", address,
            "transactionCount", count
        )));
    }

    @Operation(summary = "Get current gas price")
    @GetMapping("/gas-price")
    public ResponseEntity<Web3Response<Map<String, Object>>> getGasPrice() {
        BigInteger gasPrice = web3jService.getGasPrice();
        return ResponseEntity.ok(Web3Response.success(Map.of(
            "gasPrice", gasPrice,
            "gasPriceEth", Convert.fromWei(gasPrice.toString(), Convert.Unit.GWEI)
        )));
    }

    @Operation(summary = "Get latest block number")
    @GetMapping("/block/latest")
    public ResponseEntity<Web3Response<Map<String, Object>>> getLatestBlock() {
        BigInteger blockNumber = web3jService.getLatestBlockNumber();
        return ResponseEntity.ok(Web3Response.success(Map.of(
            "blockNumber", blockNumber
        )));
    }

    @Operation(summary = "Get block by number")
    @GetMapping("/block/{blockNumber}")
    public ResponseEntity<Web3Response<Map<String, Object>>> getBlock(
            @Parameter(description = "Block number") @PathVariable BigInteger blockNumber) {
        var block = web3jService.getBlockByNumber(blockNumber);
        return ResponseEntity.ok(Web3Response.success(Map.of(
            "blockNumber", block.getNumber(),
            "timestamp", block.getTimestamp(),
            "transactions", block.getTransactions().size()
        )));
    }

    @Operation(summary = "Get transaction by hash")
    @GetMapping("/transaction/{hash}")
    public ResponseEntity<Web3Response<Map<String, Object>>> getTransaction(
            @Parameter(description = "Transaction hash") @PathVariable String hash) {
        var transaction = web3jService.getTransactionByHash(hash);
        return ResponseEntity.ok(Web3Response.success(Map.of(
            "hash", transaction.getHash(),
            "from", transaction.getFrom(),
            "to", transaction.getTo(),
            "value", transaction.getValue(),
            "gasPrice", transaction.getGasPrice()
        )));
    }

    @Operation(summary = "Get transaction receipt")
    @GetMapping("/transaction/{hash}/receipt")
    public ResponseEntity<Web3Response<Map<String, Object>>> getTransactionReceipt(
            @Parameter(description = "Transaction hash") @PathVariable String hash) {
        var receipt = web3jService.getTransactionReceipt(hash);
        return ResponseEntity.ok(Web3Response.success(Map.of(
            "hash", receipt.getTransactionHash(),
            "blockNumber", receipt.getBlockNumber(),
            "status", receipt.getStatus(),
            "gasUsed", receipt.getGasUsed()
        )));
    }

    @Operation(summary = "Estimate gas for a transaction")
    @GetMapping("/estimate-gas")
    public ResponseEntity<Web3Response<Map<String, Object>>> estimateGas(
            @Parameter(description = "From address") @RequestParam String from,
            @Parameter(description = "To address") @RequestParam String to,
            @Parameter(description = "Amount in ETH") @RequestParam BigDecimal amount) {
        BigInteger gas = web3jService.estimateGas(from, to, amount);
        return ResponseEntity.ok(Web3Response.success(Map.of(
            "from", from,
            "to", to,
            "amount", amount,
            "estimatedGas", gas
        )));
    }

    @Operation(summary = "Get network information")
    @GetMapping("/network")
    public ResponseEntity<Web3Response<Map<String, Object>>> getNetworkInfo() {
        return ResponseEntity.ok(Web3Response.success(Map.of(
            "networkId", web3jService.getNetworkId(),
            "isSyncing", web3jService.isNodeSyncing()
        )));
    }
}
