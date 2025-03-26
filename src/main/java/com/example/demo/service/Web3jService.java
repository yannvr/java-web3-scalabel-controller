package com.example.demo.service;

import com.example.demo.config.Web3jConfig;
import com.example.demo.exception.Web3jException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@Transactional
public class Web3jService implements IWeb3jService {

    private final Web3j web3j;
    private final Web3jConfig config;

    public Web3jService(Web3j web3j, Web3jConfig config) {
        this.web3j = web3j;
        this.config = config;
    }

    @Override
    @Cacheable(value = "ethBalance", key = "#address")
    @Retryable(
        value = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public BigDecimal getEthBalance(String address) {
        try {
            BigInteger balanceWei = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST)
                .send()
                .getBalance();
            return Convert.fromWei(balanceWei.toString(), Convert.Unit.ETHER);
        } catch (Exception e) {
            log.error("Error fetching ETH balance for address: {}", address, e);
            throw new Web3jException("Failed to fetch ETH balance", e);
        }
    }

    @Override
    @Async
    public CompletableFuture<BigDecimal> getEthBalanceAsync(String address) {
        return CompletableFuture.completedFuture(getEthBalance(address));
    }

    @Override
    @Cacheable(value = "transactionCount", key = "#address")
    @Retryable(
        value = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public BigInteger getTransactionCount(String address) {
        try {
            return web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST)
                .send()
                .getTransactionCount();
        } catch (Exception e) {
            log.error("Error fetching transaction count for address: {}", address, e);
            throw new Web3jException("Failed to fetch transaction count", e);
        }
    }

    @Override
    @Cacheable(value = "gasPrice", ttl = 60)
    @Retryable(
        value = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public BigInteger getGasPrice() {
        try {
            return web3j.ethGasPrice()
                .send()
                .getGasPrice();
        } catch (Exception e) {
            log.error("Error fetching gas price", e);
            throw new Web3jException("Failed to fetch gas price", e);
        }
    }

    @Override
    @Cacheable(value = "latestBlock", ttl = 5)
    @Retryable(
        value = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public BigInteger getLatestBlockNumber() {
        try {
            return web3j.ethBlockNumber()
                .send()
                .getBlockNumber();
        } catch (Exception e) {
            log.error("Error fetching latest block number", e);
            throw new Web3jException("Failed to fetch latest block number", e);
        }
    }

    @Override
    @Cacheable(value = "block", key = "#blockNumber")
    @Retryable(
        value = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public EthBlock.Block getBlockByNumber(BigInteger blockNumber) {
        try {
            return web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber), true)
                .send()
                .getBlock();
        } catch (Exception e) {
            log.error("Error fetching block by number: {}", blockNumber, e);
            throw new Web3jException("Failed to fetch block", e);
        }
    }

    @Override
    @Cacheable(value = "transactionReceipt", key = "#transactionHash")
    @Retryable(
        value = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public TransactionReceipt getTransactionReceipt(String transactionHash) {
        try {
            return web3j.ethGetTransactionReceipt(transactionHash)
                .send()
                .getTransactionReceipt()
                .orElseThrow(() -> new Web3jException("Transaction receipt not found"));
        } catch (Exception e) {
            log.error("Error fetching transaction receipt for hash: {}", transactionHash, e);
            throw new Web3jException("Failed to fetch transaction receipt", e);
        }
    }

    @Override
    @Cacheable(value = "transaction", key = "#transactionHash")
    @Retryable(
        value = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public Transaction getTransactionByHash(String transactionHash) {
        try {
            return web3j.ethGetTransactionByHash(transactionHash)
                .send()
                .getTransaction()
                .orElseThrow(() -> new Web3jException("Transaction not found"));
        } catch (Exception e) {
            log.error("Error fetching transaction for hash: {}", transactionHash, e);
            throw new Web3jException("Failed to fetch transaction", e);
        }
    }

    @Override
    @Async
    public CompletableFuture<List<Transaction>> getPendingTransactionsAsync() {
        return CompletableFuture.completedFuture(getPendingTransactions());
    }

    @Override
    @Retryable(
        value = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public List<Transaction> getPendingTransactions() {
        try {
            return web3j.ethPendingTransactionHash()
                .send()
                .getTransactionHashes()
                .stream()
                .map(this::getTransactionByHash)
                .toList();
        } catch (Exception e) {
            log.error("Error fetching pending transactions", e);
            throw new Web3jException("Failed to fetch pending transactions", e);
        }
    }

    @Override
    @Retryable(
        value = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public BigInteger estimateGas(String from, String to, BigDecimal amount) {
        try {
            Transaction transaction = Transaction.createEtherTransaction(
                from,
                getTransactionCount(from),
                getGasPrice(),
                BigInteger.valueOf(21000),
                to,
                Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger()
            );

            return web3j.ethEstimateGas(transaction)
                .send()
                .getAmountUsed();
        } catch (Exception e) {
            log.error("Error estimating gas for transaction from {} to {}", from, to, e);
            throw new Web3jException("Failed to estimate gas", e);
        }
    }

    @Override
    @Cacheable(value = "nodeSync", ttl = 30)
    @Retryable(
        value = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public boolean isNodeSyncing() {
        try {
            return web3j.ethSyncing()
                .send()
                .isSyncing();
        } catch (Exception e) {
            log.error("Error checking node sync status", e);
            throw new Web3jException("Failed to check node sync status", e);
        }
    }

    @Override
    @Cacheable(value = "networkId", ttl = 3600)
    @Retryable(
        value = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public String getNetworkId() {
        try {
            return web3j.netVersion()
                .send()
                .getNetVersion();
        } catch (Exception e) {
            log.error("Error fetching network ID", e);
            throw new Web3jException("Failed to fetch network ID", e);
        }
    }
}
