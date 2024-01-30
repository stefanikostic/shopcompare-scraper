package com.shopcompare.scraper.rabbitmq.service;

import com.shopcompare.scraper.rabbitmq.model.Product;
import com.shopcompare.scraper.rabbitmq.configuration.RabbitMQProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Producer class that sends products to RabbitMQ.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RabbitMQProductProducer {

    private final RabbitMQProperties rabbitMQProperties;

    private final RabbitTemplate rabbitTemplate;

    /**
     * Converts sets of product and sends them to RabbitMQ queue.
     *
     * @param products set of {@link Product}.
     */
    public void sendMessage(Set<Product> products) {
        log.info(String.format("Message sent -> %s", products.size()));
        rabbitTemplate.convertAndSend(rabbitMQProperties.getExchangeName(), rabbitMQProperties.getRoutingKey(),
                products);
    }
}
