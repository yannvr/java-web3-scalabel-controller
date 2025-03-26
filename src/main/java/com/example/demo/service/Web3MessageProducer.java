package com.example.demo.service;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dto.Web3Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class Web3MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public CompletableFuture<Void> sendOperation(Web3Operation operation) {
        return CompletableFuture.runAsync(() -> {
            try {
                rabbitTemplate.convertAndSend(
                    RabbitMQConfig.WEB3_EXCHANGE,
                    RabbitMQConfig.WEB3_ROUTING_KEY,
                    operation
                );
                log.debug("Sent operation to queue: {}", operation);
            } catch (Exception e) {
                log.error("Failed to send operation to queue: {}", operation, e);
                throw new RuntimeException("Failed to send operation to queue", e);
            }
        });
    }
}
