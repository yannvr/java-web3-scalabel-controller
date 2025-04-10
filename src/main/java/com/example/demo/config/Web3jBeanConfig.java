package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableRetry
@EnableAsync
@RequiredArgsConstructor
public class Web3jBeanConfig {

    private final Web3jConfig web3jConfig;

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(web3jConfig.getNodeUrl()));
    }

    @Bean
    public RetryTemplate retryTemplate() {
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(web3jConfig.getMaxRetries());

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(web3jConfig.getRetryDelay());

        return RetryTemplate.builder()
                .maxAttempts(web3jConfig.getMaxRetries())
                .fixedBackoff(web3jConfig.getRetryDelay())
                .build();
    }
}
