package com.shopcompare.scraper.rabbitmq.service;

import com.shopcompare.scraper.rabbitmq.model.Product;
import com.shopcompare.scraper.rabbitmq.configuration.RabbitMQProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class RabbitMQProductProducer {

    public RabbitMQProductProducer(RabbitMQProperties rabbitMQProperties, RabbitTemplate rabbitTemplate) {
        this.rabbitMQProperties = rabbitMQProperties;
        this.rabbitTemplate = rabbitTemplate;
    }

    private final RabbitMQProperties rabbitMQProperties;

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(Set<Product> products) {
        log.info(String.format("Message sent -> %s", products.size()));
        rabbitTemplate.convertAndSend(rabbitMQProperties.getExchangeName(), rabbitMQProperties.getRoutingKey(),
                products);
    }
}
