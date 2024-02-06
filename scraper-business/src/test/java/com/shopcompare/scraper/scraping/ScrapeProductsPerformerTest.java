package com.shopcompare.scraper.scraping;

import com.shopcompare.scraper.rabbitmq.model.Product;
import com.shopcompare.scraper.scraping.exception.FailedScrapingException;
import com.shopcompare.scraper.scraping.products.ScrapingProductsService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScrapeProductsPerformerTest {

    @Mock
    private ScrapingProductsService scrapingProductsService;

    @InjectMocks
    private ScrapeProductsPerformer scrapeProductsPerformer;

    @SneakyThrows
    @Test
    void shouldScrapeProductsSuccessfully() {
        // given
        Set<Product> productSet = new HashSet<>();
        Product product = new Product("TV", "Setec", "TVs", "TV-link", "TV-image",
                true, 1000.0, 800.0);
        productSet.add(product);
        when(scrapingProductsService.scrapeAndExtract(anyString(), eq("TVs"), anyString())).thenReturn(productSet);

        // when
        Set<Product> result = scrapeProductsPerformer.scrapeProducts("Setec", "TVs", "URL");

        // then
        assertThat(result).isEqualTo(productSet);
    }

    @SneakyThrows
    @Test
    void shouldCatchIOExceptionInCaseOfError() {
        // given
        when(scrapingProductsService.scrapeAndExtract(anyString(), eq("TVs"), anyString()))
                .thenThrow(IOException.class);

        // when
        // then
        assertThatExceptionOfType(FailedScrapingException.class).isThrownBy(() -> scrapeProductsPerformer.scrapeProducts(
                "Setec", "TVs", "URL"));

    }
}