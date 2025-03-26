package com.example.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ethereum")
public class Web3jConfig {
    private String nodeUrl;
    private long httpTimeout = 10000;
    private int maxRetries = 3;
    private long retryDelay = 1000;
}
