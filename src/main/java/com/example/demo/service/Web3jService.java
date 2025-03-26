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
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.Transaction;
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
            EthGetBalance balance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
            return Convert.fromWei(balance.getBalance().toString(), Convert.Unit.ETHER);
        } catch (Exception e) {
            log.error("Failed to get ETH balance for address: {}", address, e);
            throw new RuntimeException("Failed to get ETH balance", e);
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
            EthGetTransactionCount count = web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send();
            return count.getTransactionCount();
        } catch (Exception e) {
            log.error("Failed to get transaction count for address: {}", address, e);
            throw new RuntimeException("Failed to get transaction count", e);
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
            EthGasPrice gasPrice = web3j.ethGasPrice().send();
            return gasPrice.getGasPrice();
        } catch (Exception e) {
            log.error("Failed to get gas price", e);
            throw new RuntimeException("Failed to get gas price", e);
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
            EthBlockNumber blockNumber = web3j.ethBlockNumber().send();
            return blockNumber.getBlockNumber();
        } catch (Exception e) {
            log.error("Failed to get latest block number", e);
            throw new RuntimeException("Failed to get latest block number", e);
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
            EthBlock block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber), true).send();
            return block.getBlock();
        } catch (Exception e) {
            log.error("Failed to get block by number: {}", blockNumber, e);
            throw new RuntimeException("Failed to get block by number", e);
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
            EthGetTransactionReceipt receipt = web3j.ethGetTransactionReceipt(transactionHash).send();
            return receipt.getTransactionReceipt().orElse(null);
        } catch (Exception e) {
            log.error("Failed to get transaction receipt for hash: {}", transactionHash, e);
            throw new RuntimeException("Failed to get transaction receipt", e);
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
            return web3j.ethGetTransactionByHash(transactionHash).send();
        } catch (Exception e) {
            log.error("Failed to get transaction by hash: {}", transactionHash, e);
            throw new RuntimeException("Failed to get transaction by hash", e);
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

            EthEstimateGas gas = web3j.ethEstimateGas(transaction).send();
            return gas.getAmountUsed();
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
            EthSyncing syncing = web3j.ethSyncing().send();
            return syncing.isSyncing();
        } catch (Exception e) {
            log.error("Failed to check node syncing status", e);
            throw new RuntimeException("Failed to check node syncing status", e);
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
            EthChainId chainId = web3j.ethChainId().send();
            return chainId.getChainId().toString();
        } catch (Exception e) {
            log.error("Failed to get network ID", e);
            throw new RuntimeException("Failed to get network ID", e);
        }
    }
}
