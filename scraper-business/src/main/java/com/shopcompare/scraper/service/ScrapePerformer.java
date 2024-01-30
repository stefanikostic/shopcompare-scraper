package com.shopcompare.scraper.service;

import com.shopcompare.scraper.rabbitmq.model.Product;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;

@Service
public class ScrapePerformer {

    private ScrapingDataService scrapingDataService;

    public Set<Product> scrapeProducts(String shop, int category, String url) {
        try {
            scrapingDataService = ScrapingDataServiceFactory.buildDataExtractor(shop);
            return scrapingDataService.scrapeAndExtract(shop, category, url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
