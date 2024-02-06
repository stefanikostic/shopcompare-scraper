package com.shopcompare.scraper.scraping;

import com.shopcompare.scraper.scraping.products.DefaultScrapingProductsService;
import com.shopcompare.scraper.scraping.products.ScrapingProductsService;
import com.shopcompare.scraper.scraping.products.SetecScrapingProductsService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ScrapingProductsServiceFactoryTest {

    private final ScrapingProductsServiceFactory scrapingProductsServiceFactory =
            new ScrapingProductsServiceFactory(List.of(new SetecScrapingProductsService()));

    @Test
    void shouldBuildDataExtractorForSetec() {
        // given
        String shopName = "Setec";

        // when
        ScrapingProductsService result = scrapingProductsServiceFactory.buildScrapingProductsService(shopName);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(SetecScrapingProductsService.class);
    }

    @Test
    void shouldBuildDefaultDataExtractorForUnknownShop() {
        // given
        String shopName = "Setec123";

        // when
        ScrapingProductsService result = scrapingProductsServiceFactory.buildScrapingProductsService(shopName);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(DefaultScrapingProductsService.class);
    }

}