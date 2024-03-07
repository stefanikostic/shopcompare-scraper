package com.shopcompare.scraper.product.scraping;

import com.shopcompare.scraper.rabbitmq.model.Product;

import java.util.Set;

/**
 * Default implementation of {@link ScrapingProductsService}, used when resolving the {@link ScrapingProductsService} based
 * on shop name in the factory. <br/>
 * This service is used when no specific {@link ScrapingProductsService} is found for the given shop.
 */
public class DefaultScrapingProductsService implements ScrapingProductsService {
    @Override
    public Set<Product> scrapeAndExtract(String shop, String category, String url) {
        return null;
    }

    @Override
    public String shopName() {
        return null;
    }
}
