package com.shopcompare.scraper.scheduled_jobs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties("scraping")
public class ShopScrapingProperties {

    private Map<String, String> shopUrl;
}
