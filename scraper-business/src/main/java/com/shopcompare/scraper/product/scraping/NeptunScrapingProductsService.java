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
    private static final String QUOTATION_MARK = "\"";
    private static final String INVALID_REGEX_FOR_QUOTATION_MARK = "(?<!\\\\)\\\\\"";
    private static final String FEATURE_GROUPS_TITLE_REGEX = "\"FeatureGroups\":.*Title";
    private static final String CATEGORY_REGEX = "\"Category\":\\{([^}]*)}";
    public static final String MAIN_CONTAINER_ID = "mainContainer";
    public static final String PAGINATION_PAGE_CLASS_NAME = "pagination-page";
    public static final String PRODUCT_LIST_ITEM_GRID_CLASS_NAME = "product-list-item-grid";
    public static final String PRODUCT_LIST_ITEM_CONTENT_TITLE = "product-list-item__content--title";
    public static final String A_TAG_NAME = "a";
    public static final String HREF_ATTRIBUTE = "href";
    public static final String IMAGE_WRAPPER_CLASS_NAME = "imageWrapper";
    public static final String IMG_TAG_NAME = "img";
    public static final String SRC_ATTRIBUTE = "src";
    public static final String HAPPY_CARD_CLASS_NAME = "HappyCard";
    public static final String PRODUCT_PRICE_AMOUNT_VALUE = "product-price__amount--value";
    public static final String NEW_PRICE_MODEL_CLASS_NAME = "newPriceModel";

    private final ChromeDriver chromeDriver;

    @Override
    public Set<Product> scrapeAndExtract(String shop, String category, String url) {
        url += LIMIT_100_PRODUCTS_QUERY_PARAM;
        chromeDriver.get(url);
        Set<Product> products = new HashSet<>();

        WebDriverWait webDriverWait = new WebDriverWait(chromeDriver, Duration.of(5000, ChronoUnit.MILLIS));
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.ById.id(MAIN_CONTAINER_ID)));

        List<WebElement> pagesWebElements = chromeDriver.findElements(By.ByClassName.className(PAGINATION_PAGE_CLASS_NAME));
        int countPages = pagesWebElements.size() / 2;
        int page = 1;

        while (page <= countPages) {
            String paginatedUrl = url + PAGE_QUERY_PARAM + page;
            chromeDriver.get(paginatedUrl);

            webDriverWait = new WebDriverWait(chromeDriver, Duration.of(5000, ChronoUnit.MILLIS));
            webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.ById.id(MAIN_CONTAINER_ID)));

            List<WebElement> productsWebElements = chromeDriver.findElements(By.ByClassName.className(PRODUCT_LIST_ITEM_GRID_CLASS_NAME));
            for (WebElement productWebElement : productsWebElements) {

                String title = productWebElement.findElement(By.ByClassName.className(PRODUCT_LIST_ITEM_CONTENT_TITLE)).getText();
                String link = productWebElement.findElement(By.ByTagName.tagName(A_TAG_NAME)).getAttribute(HREF_ATTRIBUTE);
                String thumbnail = productWebElement.findElement(By.ByClassName.className(IMAGE_WRAPPER_CLASS_NAME))
                        .findElement(By.ByTagName.tagName(IMG_TAG_NAME)).getAttribute(SRC_ATTRIBUTE);
                WebElement happyCardWebElement = productWebElement.findElement(By.ByClassName.className(HAPPY_CARD_CLASS_NAME));
                List<WebElement> discountPriceWebElement = happyCardWebElement.findElements(By.ByClassName.className(PRODUCT_PRICE_AMOUNT_VALUE));
                Double discountPrice = null;
                if (!discountPriceWebElement.isEmpty()) {
                    String discountPriceString = discountPriceWebElement.get(0).getText();
                    String discountPriceNormalized = discountPriceString.replaceAll("\\.", "");
                    discountPrice = Double.parseDouble(discountPriceNormalized);
                }
                String regularPriceString = productWebElement.findElement(By.ByClassName.className(NEW_PRICE_MODEL_CLASS_NAME))
                        .findElement(By.ByClassName.className(PRODUCT_PRICE_AMOUNT_VALUE)).getText();
                String regularPriceNormalized = regularPriceString.replaceAll("\\.", "");
                Double regularPrice = Double.parseDouble(regularPriceNormalized);
                Product product = new Product(title, NEPTUN_SHOP_NAME, category, link, thumbnail, true, regularPrice,
                        discountPrice);
                products.add(product);
            }
            page++;
        }
        chromeDriver.quit();

        return products;
    }

    /*@Override
    public Set<Product> scrapeAndExtract(String shop, String category, String url) throws IOException {
        url += LIMIT_100_PRODUCTS_QUERY_PARAM;
        Set<Product> products = new HashSet<>();

        Document document = Jsoup.connect(url).get();
        Optional<Element> scriptOptional = document.getElementsByTag(SCRIPT).stream()
                .filter(scriptElement -> scriptElement.data().contains(SHOP_CATEGORY_MODEL)).findFirst();

        if (scriptOptional.isPresent()) {
            Element script = scriptOptional.get();
            String productData = script.data();
            productData = trimProductData(productData);

            while (!productData.isEmpty()) {
                int index = productData.indexOf(TITLE_STRING);
                if (index == -1) {
                    break;
                }
                productData = productData.substring(index);

                String title = null;
                Matcher matcher = titlePattern.matcher(productData);
                boolean matchFound = matcher.find();
                if (matchFound) {
                    String titleString = matcher.group();
                    title = titleString.substring(titleString.indexOf(COLON) + 2, titleString.length() - 1);
                }

                String link = null;
                matcher = urlPattern.matcher(productData);
                matchFound = matcher.find();
                if (matchFound) {
                    String linkString = matcher.group();
                    link = url + "/" + linkString.substring(linkString.indexOf(COLON) + 2, linkString.length() - 1);
                }

                String thumbnail = null;
                matcher = thumbnailPattern.matcher(productData);
                matchFound = matcher.find();
                if (matchFound) {
                    String thumbnailString = matcher.group();
                    thumbnail = thumbnailString.substring(thumbnailString.indexOf(COLON) + 2, thumbnailString.length() - 1);
                }

                boolean availableOnline = false;
                matcher = availableOnlinePattern.matcher(productData);
                matchFound = matcher.find();
                if (matchFound) {
                    String availableOnlineString = matcher.group();
                    String availableOnlineValue = availableOnlineString.substring(availableOnlineString.indexOf(COLON) + 1);
                    availableOnline = Boolean.parseBoolean(availableOnlineValue);
                }

                Double regularPrice = null;
                matcher = regularPricePattern.matcher(productData);
                matchFound = matcher.find();
                if (matchFound) {
                    String regularPriceString = matcher.group();
                    String regularPriceValue = regularPriceString.substring(regularPriceString.indexOf(COLON) + 1);
                    regularPrice = Double.valueOf(regularPriceValue);
                }

                Double discountPrice = null;
                matcher = discountPricePattern.matcher(productData);
                matchFound = matcher.find();
                if (matchFound) {
                    String discountPriceString = matcher.group();
                    String discountPriceValue = discountPriceString.substring(discountPriceString.indexOf(COLON) + 1);
                    discountPrice = Double.valueOf(discountPriceValue);
                }

                if (title != null) {
                    Product product = new Product(title, shop, category, link, thumbnail, availableOnline, regularPrice,
                            discountPrice);
                    products.add(product);
                }

                productData = productData.substring(TITLE_STRING.length());
            }

        }

        return products;
    }*/

    private String trimProductData(String productData) {
        productData = productData.replaceAll(INVALID_REGEX_FOR_QUOTATION_MARK, QUOTATION_MARK);
        productData = productData.replaceAll(FEATURE_GROUPS_TITLE_REGEX, StringUtils.EMPTY);
        productData = productData.replaceAll(CATEGORY_REGEX, StringUtils.EMPTY);
        return productData;
    }

    @Override
    public String shopName() {
        return NEPTUN_SHOP_NAME;
    }
}
