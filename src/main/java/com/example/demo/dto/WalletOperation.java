package com.example.demo.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class WalletOperation {
    private String id;
    private OperationType type;
    private String address;
    private BigDecimal amount;
    private String toAddress;
    private Web3Response<?> result;

    public enum OperationType {
        CREATE_WALLET,
        GET_BALANCE,
        SEND_ETH,
        GET_TRANSACTION_HISTORY
    }
}
