package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletOperation {
    private String id;
    private OperationType type;
    private String address;
    private BigDecimal amount;
    private String toAddress;
    private Web3Response<Map<String, Object>> result;

    public enum OperationType {
        CREATE_WALLET,
        GET_BALANCE,
        SEND_ETH,
        GET_TRANSACTION_HISTORY
    }
}
