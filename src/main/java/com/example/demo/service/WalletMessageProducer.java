package com.example.demo.service;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dto.WalletOperation;
import com.example.demo.dto.Web3Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletMessageProducer {

    private final RabbitTemplate rabbitTemplate;
    private final MessageConverter messageConverter;

    public CompletableFuture<Web3Response<Map<String, Object>>> sendOperation(WalletOperation operation) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MessageProperties messageProperties = new MessageProperties();
                messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
                messageProperties.setMessageId(operation.getId());
                messageProperties.setHeader("operationType", operation.getType().name());

                Message message = messageConverter.toMessage(operation, messageProperties);

                rabbitTemplate.send(
                    RabbitMQConfig.WALLET_EXCHANGE,
                    RabbitMQConfig.WALLET_ROUTING_KEY,
                    message
                );

                log.debug("Sent wallet operation to queue: {}", operation);
                return operation.getResult();
            } catch (Exception e) {
                log.error("Failed to send wallet operation to queue: {}", operation, e);
                throw new RuntimeException("Failed to send wallet operation to queue", e);
            }
        });
    }
}
