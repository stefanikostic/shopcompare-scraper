package com.shopcompare.scraper.scraping.products;

import com.shopcompare.scraper.rabbitmq.model.Product;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of {@link ScrapingProductsService} for shop Neptun.
 */
@Service
public class NeptunScrapingProductsService implements ScrapingProductsService {

    private static final String NEPTUN_SHOP_NAME = "Neptun";
    private static final String LIMIT_100_PRODUCTS_QUERY_PARAM = "?items=100";
    private static final String SCRIPT = "script";
    private static final String TITLE_STRING = "\"Title\"";
    private static final String COLON = ":";
    private static final String SHOP_CATEGORY_MODEL = "shopCategoryModel";
    private static final String QUOTATION_MARK = "\"";
    private static final String INVALID_REGEX_FOR_QUOTATION_MARK = "(?<!\\\\)\\\\\"";

    private static final Pattern titlePattern = Pattern.compile("\"Title\":\"([^\"]*)\"");
    private static final Pattern urlPattern = Pattern.compile("\"Url\":\"([^\"]*)\"");
    private static final Pattern thumbnailPattern = Pattern.compile("\"Thumbnail\":\"([^\"]*)\"");

    private static final Pattern availableOnlinePattern = Pattern.compile("\"AvailableOnline\":([^,]*)");

    private static final Pattern regularPricePattern = Pattern.compile("\"RegularPrice\":([^,]*)");

    private static final Pattern discountPricePattern = Pattern.compile("\"DiscountPrice\":([^,]*)");
    private static final String FEATURE_GROUPS_TITLE_REGEX = "\"FeatureGroups\":.*Title";
    private static final String CATEGORY_REGEX = "\"Category\":\\{([^}]*)}";


    @Override
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
                    title = matcher.group();
                }

                String link = null;
                matcher = urlPattern.matcher(productData);
                matchFound = matcher.find();
                if (matchFound) {
                    link = matcher.group();
                }

                String thumbnail = null;
                matcher = thumbnailPattern.matcher(productData);
                matchFound = matcher.find();
                if (matchFound) {
                    thumbnail = matcher.group();
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
    }

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
