package com.shopcompare.scraper.product.scraping.configuration;

import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeleniumConfiguration {


    @Bean
    public ChromeDriver driver() {
        return new ChromeDriver();
    }
}
