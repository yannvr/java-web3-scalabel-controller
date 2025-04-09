package com.example.demo.service;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dto.WalletOperation;
import com.example.demo.dto.Web3Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.web3j.crypto.WalletUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
@RabbitListener(queues = RabbitMQConfig.WALLET_QUEUE, concurrency = "10")
public class WalletService {

    private final IWeb3jService web3jService;

    @RabbitHandler
    public void processOperation(WalletOperation operation,
                               @Header(AmqpHeaders.MESSAGE_ID) String messageId,
                               @Header("operationType") String operationType) {
        log.debug("Processing wallet operation: {} with messageId: {}", operation, messageId);

        CompletableFuture.runAsync(() -> {
            try {
                switch (WalletOperation.OperationType.valueOf(operationType)) {
                    case CREATE_WALLET -> {
                        String walletFile = WalletUtils.generateNewWalletFile(
                            UUID.randomUUID().toString(),
                            null
                        );
                        operation.setResult(Web3Response.success(Map.of(
                            "walletFile", walletFile
                        )));
                    }
                    case GET_BALANCE -> {
                        BigDecimal balance = web3jService.getEthBalance(operation.getAddress());
                        operation.setResult(Web3Response.success(Map.of(
                            "address", operation.getAddress(),
                            "balance", balance
                        )));
                    }
                    case SEND_ETH -> {
                        // Implementation for sending ETH
                        operation.setResult(Web3Response.success(Map.of(
                            "from", operation.getAddress(),
                            "to", operation.getToAddress(),
                            "amount", operation.getAmount()
                        )));
                    }
                    case GET_TRANSACTION_HISTORY -> {
                        operation.setResult(Web3Response.success(Map.of(
                            "address", operation.getAddress(),
                            "message", "Transaction history feature to be implemented"
                        )));
                    }
                }
                log.debug("Wallet operation processed successfully: {}", operation);
            } catch (Exception e) {
                log.error("Failed to process wallet operation: {}", operation, e);
                operation.setResult(Web3Response.error(e.getMessage()));
            }
        });
    }
}
