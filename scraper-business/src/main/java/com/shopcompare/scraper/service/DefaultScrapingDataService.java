package com.shopcompare.scraper.service;

import com.shopcompare.scraper.rabbitmq.model.Product;

import java.util.Set;

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
