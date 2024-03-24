package com.shopcompare.scraper.category.scraping.categorymapper;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("category-mapper")
public class CategoryMapperProperties {
    private String url;
}
