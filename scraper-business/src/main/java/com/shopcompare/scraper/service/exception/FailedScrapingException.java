package com.shopcompare.scraper.service.exception;

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
