package com.example.demo.service;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dto.Web3Operation;
import com.example.demo.dto.Web3Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class Web3MessageConsumer {

    private final IWeb3jService web3jService;

    @RabbitListener(queues = RabbitMQConfig.WEB3_QUEUE, concurrency = "10")
    public void processOperation(Web3Operation operation) {
        log.debug("Processing operation: {}", operation);
        try {
            CompletableFuture.runAsync(() -> {
                try {
                    switch (operation.getType()) {
                        case GET_BALANCE -> {
                            BigDecimal balance = web3jService.getEthBalance(operation.getAddress());
                            operation.setResult(Web3Response.success(Map.of(
                                "address", operation.getAddress(),
                                "balance", balance
                            )));
                        }
                        case GET_TRANSACTION_COUNT -> {
                            BigInteger count = web3jService.getTransactionCount(operation.getAddress());
                            operation.setResult(Web3Response.success(Map.of(
                                "address", operation.getAddress(),
                                "transactionCount", count
                            )));
                        }
                        case GET_GAS_PRICE -> {
                            BigInteger gasPrice = web3jService.getGasPrice();
                            operation.setResult(Web3Response.success(Map.of(
                                "gasPrice", gasPrice,
                                "gasPriceEth", Convert.fromWei(gasPrice.toString(), Convert.Unit.GWEI)
                            )));
                        }
                        case GET_LATEST_BLOCK -> {
                            BigInteger blockNumber = web3jService.getLatestBlockNumber();
                            operation.setResult(Web3Response.success(Map.of(
                                "latestBlockNumber", blockNumber
                            )));
                        }
                        case GET_NETWORK_INFO -> {
                            operation.setResult(Web3Response.success(Map.of(
                                "networkId", web3jService.getNetworkId(),
                                "isSyncing", web3jService.isNodeSyncing(),
                                "latestBlock", web3jService.getLatestBlockNumber()
                            )));
                        }
                        case ESTIMATE_GAS -> {
                            String[] params = operation.getAddress().split(",");
                            BigInteger gasEstimate = web3jService.estimateGas(
                                params[0], // from
                                params[1], // to
                                new BigDecimal(params[2]) // value
                            );
                            operation.setResult(Web3Response.success(Map.of(
                                "from", params[0],
                                "to", params[1],
                                "value", params[2],
                                "gasEstimate", gasEstimate
                            )));
                        }
                        // Add more operation types as needed
                    }
                    log.debug("Operation processed successfully: {}", operation);
                } catch (Exception e) {
                    log.error("Failed to process operation: {}", operation, e);
                    operation.setResult(Web3Response.error(e.getMessage()));
                }
            });
        } catch (Exception e) {
            log.error("Failed to process operation: {}", operation, e);
            operation.setResult(Web3Response.error(e.getMessage()));
        }
    }
}
