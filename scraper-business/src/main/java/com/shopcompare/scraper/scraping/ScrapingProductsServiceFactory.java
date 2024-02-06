package com.shopcompare.scraper.scraping;

import com.shopcompare.scraper.scraping.products.DefaultScrapingProductsService;
import com.shopcompare.scraper.scraping.products.ScrapingProductsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Factory class for resolving the corresponding {@link ScrapingProductsService} per given shop.
 */
@Service
public class ScrapingProductsServiceFactory {

    private final Map<String, ScrapingProductsService> scrapingProductsServiceByShopName = new HashMap<>();

    public ScrapingProductsServiceFactory(List<ScrapingProductsService> scrapingProductsServices) {
        for (ScrapingProductsService scrapingProductsService : scrapingProductsServices) {
            scrapingProductsServiceByShopName.put(scrapingProductsService.shopName(), scrapingProductsService);
        }
    }

    /**
     * Resolves {@link ScrapingProductsService} based on the provided shop.
     *
     * @param shop name of the shop.
     *
     * @return custom implementation of {@link ScrapingProductsService} for the certain shop, if a corresponding service
     * does not exist, {@link DefaultScrapingProductsService} is returned.
     */
    public ScrapingProductsService buildScrapingProductsService(String shop) {
        if (scrapingProductsServiceByShopName.containsKey(shop)) {
            return scrapingProductsServiceByShopName.get(shop);
        }

        return new DefaultScrapingProductsService();
    }

}
