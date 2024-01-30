package com.shopcompare.scraper.service;

/**
 * Factory class that provides {@link ScrapingDataService} based on shop.
 */
final class ScrapingDataServiceFactory {

    private static final String SETEC = "Setec";

    /**
     * The private constructor is added to prevent instantiations of this class.
     */
    private ScrapingDataServiceFactory() {

    }

    /**
     * Resolves {@link ScrapingDataService} based on the provided shop.
     *
     * @param shop name of the shop.
     *
     * @return custom implementation of {@link ScrapingDataService} for the certain shop, if a corresponding service
     * does not exist, {@link DefaultScrapingDataService} is returned.
     */
    static ScrapingDataService buildDataExtractor(String shop) {
        if (SETEC.equals(shop)) {
            return new SetecScrapingDataService();
        }

        return new DefaultScrapingDataService();
    }
}
