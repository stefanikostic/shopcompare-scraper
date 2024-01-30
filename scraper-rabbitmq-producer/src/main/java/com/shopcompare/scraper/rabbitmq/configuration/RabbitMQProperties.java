package com.shopcompare.scraper.rabbitmq.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("rabbitmq")
public class RabbitMQProperties {

    private String queueName;

    private String exchangeName;

    private String routingKey;
}
