package com.example.demo.service;

import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IWeb3jService {
    BigDecimal getEthBalance(String address);
    CompletableFuture<BigDecimal> getEthBalanceAsync(String address);
    BigInteger getTransactionCount(String address);
    BigInteger getGasPrice();
    BigInteger getLatestBlockNumber();
    EthBlock.Block getBlockByNumber(BigInteger blockNumber);
    TransactionReceipt getTransactionReceipt(String transactionHash);
    Transaction getTransactionByHash(String transactionHash);
    List<Transaction> getPendingTransactions();
    CompletableFuture<List<Transaction>> getPendingTransactionsAsync();
    BigInteger estimateGas(String from, String to, BigDecimal amount);
    boolean isNodeSyncing();
    String getNetworkId();
}
