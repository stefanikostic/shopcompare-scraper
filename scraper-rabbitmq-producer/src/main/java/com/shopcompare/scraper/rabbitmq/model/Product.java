package com.shopcompare.scraper.rabbitmq.model;

import lombok.NonNull;

import java.io.Serializable;

/**
 * Transport POJO class for product.
 *
 * @param name name of the product.
 * @param shopName name of the shop where the product belongs to.
 * @param category name of the category where the product belongs to.
 * @param link website link of the product.
 * @param image image link of the product.
 * @param isAvailable states whether the product is available in stock
 * @param originalPrice original price of the product.
 * @param promotionalPrice promotional price of the product.
 */
public record Product(@NonNull String name,
                      @NonNull String shopName, String category, String link, String image, boolean isAvailable,
                      Double originalPrice, Double promotionalPrice) implements Serializable {

}
