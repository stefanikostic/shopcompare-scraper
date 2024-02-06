package com.shopcompare.scraper.scraping;

import com.shopcompare.scraper.rabbitmq.model.Product;
import com.shopcompare.scraper.scraping.exception.FailedScrapingException;
import com.shopcompare.scraper.scraping.products.ScrapingProductsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScrapeProductsPerformer {

    private final ScrapingProductsService scrapingProductsService;

    public Set<Product> scrapeProducts(String shop, String category, String url) {
        try {
            return scrapingProductsService.scrapeAndExtract(shop, category, url);
        } catch (Exception e) {
            log.error("{} has invalid url: {} for category: {}", shop, url, category);
            throw new FailedScrapingException(shop);
        }
    }
}
