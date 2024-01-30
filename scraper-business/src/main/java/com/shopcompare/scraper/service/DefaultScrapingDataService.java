package com.shopcompare.scraper.service;

import com.shopcompare.scraper.rabbitmq.model.Product;

import java.util.Set;

/**
 * Default implementation of {@link ScrapingDataService}, used when resolving the {@link ScrapingDataService} based
 * on shop name in the factory. <br/>
 * This service is used when no specific {@link ScrapingDataService} is found for the given shop.
 */
public class DefaultScrapingDataService implements ScrapingDataService {
    @Override
    public Set<Product> scrapeAndExtract(String shop, int categoryId, String url) {
        return null;
    }

    @Override
    public String shopName() {
        return null;
    }
}
