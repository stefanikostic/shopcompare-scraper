package com.shopcompare.scraper.scheduled_jobs;

import com.shopcompare.scraper.scraping.ScrapingProductsServiceFactory;
import com.shopcompare.scraper.scraping.model.CategoryLink;
import com.shopcompare.scraper.rabbitmq.model.Product;
import com.shopcompare.scraper.scraping.ScrapeProductsPerformer;
import com.shopcompare.scraper.rabbitmq.service.RabbitMQProductProducer;
import com.shopcompare.scraper.scraping.category.SetecCategoryLinksFetcher;
import com.shopcompare.scraper.scraping.products.ScrapingProductsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Service wrapping multiple scheduled methods dedicated for scraping products per shop.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScrapingPerformer {

    private static final String SETEC = "Setec";

    private final ScrapingProductsServiceFactory scrapingProductsServiceFactory;

    private final RabbitMQProductProducer rabbitMQProductProducer;

    private final SetecCategoryLinksFetcher setecCategoryLinksFetcher;

    /**
     * Fetches the scraping configurations for Setec shop, performs the scraping using the
     * configurations per category and sends each batch of scraped products to RabbitMQ queue.
     *
     */
    @Scheduled(fixedRate = 6000000)
    public void scrapeSetec() {
        Set<CategoryLink> categoryLinks = setecCategoryLinksFetcher.fetchCategoriesLinks("https://setec.mk");

        ScrapingProductsService scrapingProductsService =
                scrapingProductsServiceFactory.buildScrapingProductsService(SETEC);
        ScrapeProductsPerformer scrapeProductsPerformer = new ScrapeProductsPerformer(scrapingProductsService);
        for (CategoryLink categoryLink : categoryLinks) {
            Set<Product> products = scrapeProductsPerformer.scrapeProducts(SETEC, categoryLink.categoryName(),
                    categoryLink.url());
            log.info("Category: " + categoryLink.categoryName() + " Products size: " + products.size());

            rabbitMQProductProducer.sendMessage(products);
        }

    }
}
