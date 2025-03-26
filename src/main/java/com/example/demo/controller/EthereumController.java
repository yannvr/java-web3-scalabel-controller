package com.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ethereum")
@RequiredArgsConstructor
public class EthereumController {

    private final Web3j web3j;

    @GetMapping("/balance/{address}")
    public ResponseEntity<Map<String, Object>> getEthBalance(@PathVariable String address) {
        try {
            // Get balance in Wei
            BigInteger balanceWei = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST)
                .send()
                .getBalance();

            // Convert to ETH
            BigDecimal balanceEth = Convert.fromWei(balanceWei.toString(), Convert.Unit.ETHER);

            return ResponseEntity.ok(Map.of(
                "address", address,
                "balanceEth", balanceEth,
                "balanceWei", balanceWei
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of(
                    "error", "Failed to fetch ETH balance",
                    "message", e.getMessage()
                ));
        }
    }
}
