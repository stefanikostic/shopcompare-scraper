package com.shopcompare.scraper.category.scraping;


import java.util.Set;

/**
 * Service that fetches category links for certain shop from given link.
 */
public interface CategoryLinksFetcher {

    /**
     * Gets category details for a shop.
     * @param url link of the shop website where the categories can be scraped from.
     *
     * @return set of {@link CategoryLink} that were scraped.
     */
    Set<CategoryLink> fetchCategoriesLinks(String url);
}
