package com.shopcompare.scraper.configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * Model that represents the scraping configuration, it consists of category and shop url of products filtered by
 * category.
 */
@Setter
@Getter
public class ScrapingPropertiesModel {

    private int category;
    private String url;

}
