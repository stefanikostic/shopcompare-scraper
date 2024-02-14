package com.shopcompare.scraper.scraping;

import com.shopcompare.scraper.rabbitmq.model.Product;
import com.shopcompare.scraper.scraping.exception.FailedScrapingException;
import com.shopcompare.scraper.scraping.products.ScrapingProductsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * Service that performs scraping products by using the provided {@link ScrapingProductsService}.
 */
@Slf4j
@RequiredArgsConstructor
public class ScrapeProductsPerformer {

    private final ScrapingProductsService scrapingProductsService;

    /**
     * Invokes the {@link ScrapingProductsService} to execute the scraping and extraction.
     * Log details and rethrows {@link FailedScrapingException} in case of error.
     *
     * @param shop shop name.
     * @param category category name.
     * @param url url of the shop website.
     *
     * @return set of scraped {@link Product}, or throws {@link FailedScrapingException} in case error occurs
     * during scraping or data extraction.
     */
    public Set<Product> scrapeProducts(String shop, String category, String url) {
        try {
            return scrapingProductsService.scrapeAndExtract(shop, category, url);
        } catch (Exception e) {
            log.error("{} has invalid url: {} for category: {}", shop, url, category);
            throw new FailedScrapingException(shop);
        }
    }
}
