package com.shopcompare.scraper.service;

final class ScrapingDataServiceFactory {

    private static final String SETEC = "Setec";

    private ScrapingDataServiceFactory() {

    }

    static ScrapingDataService buildDataExtractor(String shop) {
        if (SETEC.equals(shop)) {
            return new SetecScrapingDataService();
        }

        return new DefaultScrapingDataService();
    }
}
