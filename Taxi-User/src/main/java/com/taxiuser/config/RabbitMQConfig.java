package com.taxiuser.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.notify-driver}")
    private String notifierQueue;

    @Value("${rabbitmq.queue.reporter}")
    private String reporterQueue;

    @Value("${rabbitmq.routing.notify-driver}")
    private String notifierKey;

    @Value("${rabbitmq.routing.reporter}")
    private String reporterKey;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Bean
    public Queue notifierQueue() {
        return new Queue(notifierQueue);
    }

    @Bean
    public Queue reporterQueue() {
        return new Queue(reporterQueue);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Binding notifierBinding() {
        return BindingBuilder.bind(notifierQueue()).to(exchange()).with(notifierKey);
    }

    @Bean
    public Binding reporterBinding() {
        return BindingBuilder.bind(reporterQueue()).to(exchange()).with(reporterKey);
    }

}

