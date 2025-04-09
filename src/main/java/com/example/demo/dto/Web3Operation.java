package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web3Operation {
    private String id;
    private OperationType type;
    private String address;
    private Web3Response<Map<String, Object>> result;

    public enum OperationType {
        GET_BALANCE,
        GET_TRANSACTION_COUNT,
        GET_GAS_PRICE,
        GET_LATEST_BLOCK,
        GET_BLOCK,
        GET_TRANSACTION,
        GET_TRANSACTION_RECEIPT,
        ESTIMATE_GAS,
        GET_NETWORK_INFO
    }
}
