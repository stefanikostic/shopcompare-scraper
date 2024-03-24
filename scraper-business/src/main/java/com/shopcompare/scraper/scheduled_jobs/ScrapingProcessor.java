package com.shopcompare.scraper.scheduled_jobs;

import com.shopcompare.scraper.category.scraping.CategoryLink;
import com.shopcompare.scraper.category.scraping.NeptunCategoryLinksFetcher;
import com.shopcompare.scraper.category.scraping.SetecCategoryLinksFetcher;
import com.shopcompare.scraper.category.scraping.categorymapper.CategoryMapperInvoker;
import com.shopcompare.scraper.product.scraping.ScrapingProductsServiceFactory;
import com.shopcompare.scraper.rabbitmq.model.Product;
import com.shopcompare.scraper.product.scraping.ScrapeProductsPerformer;
import com.shopcompare.scraper.rabbitmq.service.RabbitMQProductProducer;
import com.shopcompare.scraper.product.scraping.ScrapingProductsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Service wrapping multiple scheduled methods dedicated for scraping products per shop and sending them to RabbitMQ
 * queue.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScrapingProcessor {

    private static final String SETEC = "Setec";
    private static final String NEPTUN = "Neptun";

    private final ShopScrapingProperties shopScrapingProperties;
    private final ScrapingProductsServiceFactory scrapingProductsServiceFactory;
    private final RabbitMQProductProducer rabbitMQProductProducer;
    private final SetecCategoryLinksFetcher setecCategoryLinksFetcher;
    private final NeptunCategoryLinksFetcher neptunCategoryLinksFetcher;
    private final CategoryMapperInvoker categoryMapperInvoker;

    /**
     * Fetches the scraping configuration for Neptun shop, maps the fetched categories to predefined categories.
     * After categories are scraped and mapped, it uses categories links from the shop website to scrape the products.
     * As soon as products per category are scraped, the results are sent to RabbitMQ queue.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void scrapeDataFromShopNeptun() {
        String shopUrl = shopScrapingProperties.getShopUrl().get(NEPTUN);
        Set<CategoryLink> categoryLinks = neptunCategoryLinksFetcher.fetchCategoriesLinks(shopUrl);
        List<CategoryLink> mappedCategoryLinks = categoryMapperInvoker.formMappedCategories(categoryLinks);

        ScrapingProductsService scrapingProductsService =
                scrapingProductsServiceFactory.buildScrapingProductsService(NEPTUN);
        ScrapeProductsPerformer scrapeProductsPerformer = new ScrapeProductsPerformer(scrapingProductsService);

        for (CategoryLink categoryLink : mappedCategoryLinks) {
            Set<Product> products = scrapeProductsPerformer.scrapeProducts(
                    categoryLink.shopName(),
                    categoryLink.categoryName(), categoryLink.url());
            log.info("Category: {}. Products size: {}.", categoryLink.categoryName(), products.size());

            rabbitMQProductProducer.sendMessage(products);
        }
    }

    /**
     * Fetches the scraping configurations for Setec shop, maps the fetched categories to predefined categories.
     * Afterward, it performs the product scraping using the configurations per category and sends each batch of scraped
     * products to RabbitMQ queue.
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void scrapeDataFromShopSetec() {
        String shopUrl = shopScrapingProperties.getShopUrl().get(SETEC);
        Set<CategoryLink> categoryLinks = setecCategoryLinksFetcher.fetchCategoriesLinks(shopUrl);
        List<CategoryLink> mappedCategoryLinks = categoryMapperInvoker.formMappedCategories(categoryLinks);

        ScrapingProductsService scrapingProductsService =
                scrapingProductsServiceFactory.buildScrapingProductsService(SETEC);
        ScrapeProductsPerformer scrapeProductsPerformer = new ScrapeProductsPerformer(scrapingProductsService);

        for (CategoryLink categoryLink : mappedCategoryLinks) {
            Set<Product> products = scrapeProductsPerformer.scrapeProducts(
                    categoryLink.shopName(),
                    categoryLink.categoryName(), categoryLink.url());
            log.info("Category: {}. Products size: {}.", categoryLink.categoryName(), products.size());

            rabbitMQProductProducer.sendMessage(products);
        }
    }
}
