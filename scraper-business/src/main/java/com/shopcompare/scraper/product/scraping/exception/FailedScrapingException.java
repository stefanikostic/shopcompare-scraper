package com.shopcompare.scraper.product.scraping.exception;

import lombok.Getter;

/**
 * Custom {@link RuntimeException} to signal that scraping failed.
 */
@Getter
public class FailedScrapingException extends RuntimeException {

    private final String errorMessage;

    public FailedScrapingException(String shopName) {
        this.errorMessage = "Failed to complete scraping of shop " + shopName;
    }
}
