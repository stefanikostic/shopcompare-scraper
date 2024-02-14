package com.shopcompare.scraper.scraping.products;

import com.shopcompare.scraper.rabbitmq.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link ScrapingProductsService} for Setec shop.
 */
@Slf4j
@Service
public class SetecScrapingProductsService implements ScrapingProductsService {

    private static final String PRODUCT = "product";
    private static final String NAME = "name";
    private static final String A_TAG = "a";
    private static final String IMAGE_CLASS = "image";
    private static final String HREF_ATTRIBUTE = "href";
    private static final String IMG_TAG = "img";
    private static final String ATTR_DATA_ECHO = "data-echo";
    private static final String ON_LAGER_CLASS = "ima_zaliha";
    private static final String OLD_PRICE_CLASS = "price-old-new";
    private static final String NEW_PRICE_CLASS = "price-new-new";
    private static final String SETEC_SHOP_NAME = "Setec";
    private static final String LIMIT_100_PRODUCTS_QUERY_PARAM = "&limit=100";
    private static final String PAGE_QUERY_PARAM = "&page=";
    private static final String CASH_PRICE = "cena_za_kesh";
    private static final String COMMA_SYMBOL = ",";
    private static final String MULTIPLE_SPACE_REGEX = "\\s+";

    /**
     * Traverses through all pages of the given shop website filtered by certain category id. <br/>
     * {@inheritDoc}
     */
    @Override
    public Set<Product> scrapeAndExtract(String shop, String category, String url) throws IOException {
        url += LIMIT_100_PRODUCTS_QUERY_PARAM;
        Set<Product> products = new HashSet<>();

        Document document = Jsoup.connect(url).get();

        int page = 1;
        while (true) {
            if (page > 1) {
                String paginatedUrl = url + PAGE_QUERY_PARAM + page;
                document = Jsoup.connect(paginatedUrl).get();
            }

            Elements productElements = document.getElementsByClass(PRODUCT);
            if (CollectionUtils.isEmpty(productElements)) {
                break;
            }

            List<Product> productsFromPage = extractProducts(shop, category, productElements);
            products.addAll(productsFromPage);

            page++;
        }

        return products;
    }

    private List<Product> extractProducts(String shop, String category, Elements productElements) {
        List<Product> products = new ArrayList<>();
        for (Element productElement : productElements) {

            String name = resolveProductName(productElement);

            String link = null;
            String image = null;
            Element divProductImage = productElement.getElementsByClass(IMAGE_CLASS).first();
            if (divProductImage != null) {
                Element linkElement = divProductImage.getElementsByTag(A_TAG).first();
                link = linkElement != null ? linkElement.attr(HREF_ATTRIBUTE) : null;

                Element imageElement = divProductImage.getElementsByTag(IMG_TAG).first();
                image = imageElement != null ? imageElement.attr(ATTR_DATA_ECHO) : null;
            }

            boolean isAvailable = !CollectionUtils.isEmpty(productElement.getElementsByClass(ON_LAGER_CLASS));

            Double originalPrice = resolveOriginalPrice(productElement);

            Double promotionalPrice = resolvePromotionalPrice(productElement);

            if (StringUtils.isNotEmpty(name) && originalPrice != null) {
                Product product = new Product(name, shop, category, link, image, isAvailable, originalPrice,
                        promotionalPrice);
                products.add(product);
            }
        }

        return products;
    }

    private String resolveProductName(Element productElement) {
        String name = null;
        Element nameElement = productElement.getElementsByClass(NAME).first();
        if (nameElement != null) {
            Element linkNameElement = nameElement.getElementsByTag(A_TAG).first();
            name = linkNameElement != null ? linkNameElement.text() : null;
        }
        return name;
    }

    private Double resolveOriginalPrice(Element productElement) {
        Element originalPriceElement = productElement.getElementsByClass(OLD_PRICE_CLASS).first();
        if (originalPriceElement == null) {
            originalPriceElement = productElement.getElementsByClass(CASH_PRICE).first();
        }

        return formatPrice(originalPriceElement);
    }

    private Double resolvePromotionalPrice(Element productElement) {
        Element promotionalPriceElement = productElement.getElementsByClass(NEW_PRICE_CLASS).first();
        return formatPrice(promotionalPriceElement);
    }

    private Double formatPrice(Element originalPriceElement) {
        Double originalPrice = null;
        if (originalPriceElement != null) {
            String originalPriceText = originalPriceElement.text().split(MULTIPLE_SPACE_REGEX)[0];
            originalPriceText = originalPriceText.replace(COMMA_SYMBOL, StringUtils.EMPTY);
            originalPrice = Double.parseDouble(originalPriceText);
        }
        return originalPrice;
    }
    
    @Override
    public String shopName() {
        return SETEC_SHOP_NAME;
    }
}
