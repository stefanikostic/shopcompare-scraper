package com.shopcompare.scraper.scraping.model;

/**
 * POJO for storing category details.
 *
 * @param url link of products that belong to the category
 * @param categoryName name of the category
 * @param shopName name of the shop
 */
public record CategoryLink(String url, String categoryName, String shopName) {
}
