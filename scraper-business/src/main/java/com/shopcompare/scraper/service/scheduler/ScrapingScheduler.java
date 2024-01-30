package com.shopcompare.scraper.service.scheduler;

import com.shopcompare.scraper.configuration.ScrapingProperties;
import com.shopcompare.scraper.configuration.ScrapingPropertiesModel;
import com.shopcompare.scraper.rabbitmq.model.Product;
import com.shopcompare.scraper.service.ScrapePerformer;
import com.shopcompare.scraper.rabbitmq.service.RabbitMQProductProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Service wrapping multiple scheduled methods dedicated for scraping products per shop.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScrapingScheduler {

    private static final String SETEC = "Setec";

    private final ScrapingProperties scrapingProperties;

    private final ScrapePerformer scrapePerformer;

    private final RabbitMQProductProducer rabbitMQProductProducer;

    /**
     * Fetches the scraping configurations for Setec shop, performs the scraping using the
     * configurations per category and sends each batch of scraped products to RabbitMQ queue.
     *
     */
    @Scheduled(fixedRate = 60000)
    public void scrapeSetec() {
        List<ScrapingPropertiesModel> scrapingPropertiesModels =
                scrapingProperties.getScrapingConfigurations().get(SETEC);
        for (ScrapingPropertiesModel model : scrapingPropertiesModels) {
            Set<Product> products = scrapePerformer.scrapeProducts(SETEC, model.getCategory(), model.getUrl());
            log.info("Products size: " + products.size());
            rabbitMQProductProducer.sendMessage(products);
        }

    }
}