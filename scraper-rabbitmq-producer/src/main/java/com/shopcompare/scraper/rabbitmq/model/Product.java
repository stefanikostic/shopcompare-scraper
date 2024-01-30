package com.shopcompare.scraper.rabbitmq.model;

import lombok.NonNull;

import java.io.Serializable;

public record Product(@NonNull String name,
                      @NonNull String shopName, int categoryId, String link, String image, boolean isAvailable,
                      Double originalPrice, Double promotionalPrice) implements Serializable {

}
