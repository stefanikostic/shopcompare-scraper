package com.shopcompare.scraper.product.scraping;

import com.shopcompare.scraper.rabbitmq.model.Product;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link ScrapingProductsService} for shop Neptun.
 */
@Service
@RequiredArgsConstructor
public class NeptunScrapingProductsService implements ScrapingProductsService {

    private static final String NEPTUN_SHOP_NAME = "Neptun";
    private static final String LIMIT_100_PRODUCTS_QUERY_PARAM = "?items=100";

    private static final String PAGE_QUERY_PARAM = "&page=";
    private static final String MAIN_CONTAINER_ID = "mainContainer";
    private static final String PAGINATION_PAGE_CLASS_NAME = "pagination-page";
    private static final String PRODUCT_LIST_ITEM_GRID_CLASS_NAME = "product-list-item-grid";
    private static final String PRODUCT_LIST_ITEM_CONTENT_TITLE = "product-list-item__content--title";
    private static final String A_TAG_NAME = "a";
    private static final String HREF_ATTRIBUTE = "href";
    private static final String IMAGE_WRAPPER_CLASS_NAME = "imageWrapper";
    private static final String IMG_TAG_NAME = "img";
    private static final String SRC_ATTRIBUTE = "src";
    private static final String HAPPY_CARD_CLASS_NAME = "HappyCard";
    private static final String PRODUCT_PRICE_AMOUNT_VALUE = "product-price__amount--value";
    private static final String NEW_PRICE_MODEL_CLASS_NAME = "newPriceModel";
    private static final int WEB_DRIVER_WAIT_MILLIS = 5000;
    private static final String DOT_REGEX = "\\.";

    private final ChromeDriver chromeDriver;

    @Override
    public Set<Product> scrapeAndExtract(String shop, String category, String url) {
        url += LIMIT_100_PRODUCTS_QUERY_PARAM;
        chromeDriver.get(url);
        Set<Product> products = new HashSet<>();

        WebDriverWait webDriverWait = new WebDriverWait(chromeDriver, Duration.of(WEB_DRIVER_WAIT_MILLIS,
                ChronoUnit.MILLIS));
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.ById.id(MAIN_CONTAINER_ID)));

        List<WebElement> pagesWebElements =
                chromeDriver.findElements(By.ByClassName.className(PAGINATION_PAGE_CLASS_NAME));
        int countPages = pagesWebElements.size() / 2;
        int page = 1;

        while (page <= countPages) {
            String paginatedUrl = url + PAGE_QUERY_PARAM + page;
            chromeDriver.get(paginatedUrl);

            webDriverWait = new WebDriverWait(chromeDriver, Duration.of(WEB_DRIVER_WAIT_MILLIS, ChronoUnit.MILLIS));
            webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.ById.id(MAIN_CONTAINER_ID)));

            List<WebElement> productsWebElements =
                    chromeDriver.findElements(By.ByClassName.className(PRODUCT_LIST_ITEM_GRID_CLASS_NAME));
            for (WebElement productWebElement : productsWebElements) {

                String title =
                        productWebElement.findElement(
                                By.ByClassName.className(PRODUCT_LIST_ITEM_CONTENT_TITLE)).getText();
                String link =
                        productWebElement.findElement(By.ByTagName.tagName(A_TAG_NAME)).getAttribute(HREF_ATTRIBUTE);
                String thumbnail =
                        productWebElement.findElement(By.ByClassName.className(IMAGE_WRAPPER_CLASS_NAME)).findElement(
                                By.ByTagName.tagName(IMG_TAG_NAME)).getAttribute(SRC_ATTRIBUTE);
                WebElement happyCardWebElement =
                        productWebElement.findElement(By.ByClassName.className(HAPPY_CARD_CLASS_NAME));
                List<WebElement> discountPriceWebElement =
                        happyCardWebElement.findElements(By.ByClassName.className(PRODUCT_PRICE_AMOUNT_VALUE));
                Double discountPrice = null;
                if (!discountPriceWebElement.isEmpty()) {
                    String discountPriceString = discountPriceWebElement.get(0).getText();
                    String discountPriceNormalized = discountPriceString.replaceAll(DOT_REGEX, "");
                    discountPrice = Double.parseDouble(discountPriceNormalized);
                }
                String regularPriceString =
                        productWebElement.findElement(By.ByClassName.className(NEW_PRICE_MODEL_CLASS_NAME)).findElement(
                                By.ByClassName.className(PRODUCT_PRICE_AMOUNT_VALUE)).getText();
                String regularPriceNormalized = regularPriceString.replaceAll(DOT_REGEX, "");
                Double regularPrice = Double.parseDouble(regularPriceNormalized);
                Product product = new Product(title, NEPTUN_SHOP_NAME, category, link, thumbnail, true, regularPrice,
                        discountPrice);
                products.add(product);
            }
            page++;
        }

        return products;
    }

    @Override
    public String shopName() {
        return NEPTUN_SHOP_NAME;
    }
}
