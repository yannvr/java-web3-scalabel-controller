package com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    public static final String WALLET_QUEUE = "wallet.operations";
    public static final String WALLET_EXCHANGE = "wallet.exchange";
    public static final String WALLET_ROUTING_KEY = "wallet.operation";

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);

        // High throughput settings
        connectionFactory.setChannelCacheSize(25);
        connectionFactory.setRequestedHeartBeat(60);

        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        // High throughput settings
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReplyTimeout(60000);

        return rabbitTemplate;
    }

    @Bean
    public Queue walletQueue() {
        return QueueBuilder.durable(WALLET_QUEUE)
                .withArgument("x-max-length", 100000)
                .withArgument("x-overflow", "reject-publish")
                .build();
    }

    @Bean
    public DirectExchange walletExchange() {
        return new DirectExchange(WALLET_EXCHANGE);
    }

    @Bean
    public Binding binding(Queue walletQueue, DirectExchange walletExchange) {
        return BindingBuilder
                .bind(walletQueue)
                .to(walletExchange)
                .with(WALLET_ROUTING_KEY);
    }
}
