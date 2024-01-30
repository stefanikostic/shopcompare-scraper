package com.shopcompare.scraper.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties("root")
@PropertySource("classpath:scraping-configurations.properties")
public class ScrapingProperties {

    private Map<String, List<ScrapingPropertiesModel>> scrapingConfigurations;
}
