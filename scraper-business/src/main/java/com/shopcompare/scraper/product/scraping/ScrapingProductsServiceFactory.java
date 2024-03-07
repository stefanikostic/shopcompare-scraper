package com.shopcompare.scraper.product.scraping;

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

    /**
     * Constructs the {@link ScrapingProductsServiceFactory} instance by collecting all bean implementations
     * of {@link ScrapingProductsService} and using them to populate the
     * {@link ScrapingProductsServiceFactory#scrapingProductsServiceByShopName}.
     *
     * @param scrapingProductsServices all registered beans that are instances of {@link ScrapingProductsService}.
     */
    public ScrapingProductsServiceFactory(List<ScrapingProductsService> scrapingProductsServices) {
        for (ScrapingProductsService scrapingProductsService : scrapingProductsServices) {
            scrapingProductsServiceByShopName.put(scrapingProductsService.shopName(), scrapingProductsService);
        }
    }

    /**
     * Looks for a corresponding {@link ScrapingProductsService} in the map of all registered
     * {@link ScrapingProductsService} beans by the corresponding shop name.
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
