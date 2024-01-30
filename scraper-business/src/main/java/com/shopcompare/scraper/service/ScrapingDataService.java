package com.shopcompare.scraper.service;

import com.shopcompare.scraper.rabbitmq.model.Product;

import java.io.IOException;
import java.util.Set;

/**
 * Service that scrapes the content from given URL and extracts the product data.
 * Since the source content is different, the implementations of this interface should contain custom logic
 * for scraping the whole product content of a category and extract the results.
 */
public interface ScrapingDataService {

    /**
     * <p>Performs scraping of websites by using the url passed as argument and its transformations. These URL
     * transformations can occur in case there is pagination in the website accessed by the original URL and
     * usually this is resolved by appending the page number. </p>
     * Based on the shop website content, this method can contain custom logic in order to traverse through all
     * products of given categoryId.
     * <p>Finally, while traversing through the product elements, this method performs data extraction by persisting the
     * product into the set of {@link Product}.</p>
     *
     * @param shop name of the shop.
     * @param categoryId category id.
     * @param url website url, can be used to a construct a new url for another scraping to resolve website pagination.
     *
     * @return set of {@link Product} which represent all product elements of given category that were scraped for
     * given shop.
     *
     * @throws IOException in case error occurs during executing the get request.
     */
    Set<Product> scrapeAndExtract(String shop, int categoryId, String url) throws IOException;

    String shopName();
}
