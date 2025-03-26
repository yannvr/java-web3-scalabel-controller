package com.example.demo;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dto.WalletOperation;
import com.example.demo.dto.Web3Response;
import com.example.demo.service.WalletMessageProducer;
import com.example.demo.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class WalletControllerTest {

    @Autowired
    private WalletMessageProducer messageProducer;

    @Autowired
    private WalletService walletService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testCreateWalletFlow() throws InterruptedException {
        // Create test operation
        WalletOperation operation = new WalletOperation();
        operation.setId("test-id-1");
        operation.setType(WalletOperation.OperationType.CREATE_WALLET);

        // Send operation to queue
        CompletableFuture<Void> future = messageProducer.sendOperation(operation);
        future.join();

        // Wait for processing
        Thread.sleep(1000);

        // Verify operation was processed
        assertNotNull(operation.getResult());
        assertTrue(operation.getResult().isSuccess());
        assertNotNull(operation.getResult().getData());
        assertTrue(operation.getResult().getData().containsKey("walletFile"));
    }

    @Test
    public void testGetBalanceFlow() throws InterruptedException {
        // Create test operation
        WalletOperation operation = new WalletOperation();
        operation.setId("test-id-2");
        operation.setType(WalletOperation.OperationType.GET_BALANCE);
        operation.setAddress("0x1234567890abcdef1234567890abcdef12345678");

        // Send operation to queue
        CompletableFuture<Void> future = messageProducer.sendOperation(operation);
        future.join();

        // Wait for processing
        Thread.sleep(1000);

        // Verify operation was processed
        assertNotNull(operation.getResult());
        assertTrue(operation.getResult().isSuccess());
        assertNotNull(operation.getResult().getData());
        assertTrue(operation.getResult().getData().containsKey("balance"));
    }

    @Test
    public void testSendEthFlow() throws InterruptedException {
        // Create test operation
        WalletOperation operation = new WalletOperation();
        operation.setId("test-id-3");
        operation.setType(WalletOperation.OperationType.SEND_ETH);
        operation.setAddress("0x1234567890abcdef1234567890abcdef12345678");
        operation.setToAddress("0xabcdef1234567890abcdef1234567890abcdef12");
        operation.setAmount(new BigDecimal("0.1"));

        // Send operation to queue
        CompletableFuture<Void> future = messageProducer.sendOperation(operation);
        future.join();

        // Wait for processing
        Thread.sleep(1000);

        // Verify operation was processed
        assertNotNull(operation.getResult());
        assertTrue(operation.getResult().isSuccess());
        assertNotNull(operation.getResult().getData());
        assertTrue(operation.getResult().getData().containsKey("amount"));
    }

    @Test
    public void testMessageProperties() {
        // Create test operation
        WalletOperation operation = new WalletOperation();
        operation.setId("test-id-4");
        operation.setType(WalletOperation.OperationType.GET_BALANCE);
        operation.setAddress("0x1234567890abcdef1234567890abcdef12345678");

        // Send operation to queue
        CompletableFuture<Void> future = messageProducer.sendOperation(operation);
        future.join();

        // Verify message was sent with correct properties
        verify(rabbitTemplate, times(1)).send(
            eq(RabbitMQConfig.WALLET_EXCHANGE),
            eq(RabbitMQConfig.WALLET_ROUTING_KEY),
            argThat(message -> {
                assertEquals(operation.getId(), message.getMessageProperties().getMessageId());
                assertEquals("application/json", message.getMessageProperties().getContentType());
                assertEquals(operation.getType().name(), message.getMessageProperties().getHeader("operationType"));
                return true;
            })
        );
    }

    @Test
    public void testErrorHandling() throws InterruptedException {
        // Create test operation with invalid address
        WalletOperation operation = new WalletOperation();
        operation.setId("test-id-5");
        operation.setType(WalletOperation.OperationType.GET_BALANCE);
        operation.setAddress("invalid-address");

        // Send operation to queue
        CompletableFuture<Void> future = messageProducer.sendOperation(operation);
        future.join();

        // Wait for processing
        Thread.sleep(1000);

        // Verify error handling
        assertNotNull(operation.getResult());
        assertFalse(operation.getResult().isSuccess());
        assertNotNull(operation.getResult().getError());
    }
}
