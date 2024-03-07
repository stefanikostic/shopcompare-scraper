package com.shopcompare.scraper.category.scraping;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link CategoryLinksFetcher} for shop Setec.
 */
@Service
@Slf4j
public class SetecCategoryLinksFetcher implements CategoryLinksFetcher {

    private static final String SETEC = "Setec";
    private static final String LI_TAG = "li";
    private static final String A_TAG = "a";
    private static final String UL_TAG = "ul";
    private static final String HREF_ATTRIBUTE_KEY = "href";
    private static final String MENU_CLASS_NAME = "menu";
    private static final String MEGAMENU_WRAPPER_CLASS_NAME = "megamenu-wrapper";

    @Override
    public Set<CategoryLink> fetchCategoriesLinks(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            Element menuWrapper = document.getElementsByClass(MEGAMENU_WRAPPER_CLASS_NAME).first();
            Set<CategoryLink> categoryLinks = new HashSet<>();
            if (menuWrapper != null) {
                Elements menuElements = menuWrapper.getElementsByClass(MENU_CLASS_NAME);

                Elements allListElements = new Elements();
                for (Element menuElement : menuElements) {
                    Elements listElements = menuElement.getElementsByTag(LI_TAG);
                    allListElements.addAll(listElements);
                }

                Elements linkElements = new Elements();
                for (Element liElement : allListElements) {
                    linkElements.addAll(resolveLinkElements(liElement));
                }

                for (Element linkElement : linkElements) {
                    CategoryLink categoryLink = new CategoryLink(linkElement.attr(HREF_ATTRIBUTE_KEY),
                            linkElement.text(), SETEC);
                    categoryLinks.add(categoryLink);
                }
            }

            return categoryLinks;
        } catch (Exception e) {
            log.error("Invalid url: {}. Failed scraping categories for shop {}", url, SETEC);
            throw new FailedScrapingCategoryException(SETEC);
        }
    }

    private Elements resolveLinkElements(Element liElement) {
        Elements ulElements = liElement.getElementsByTag(UL_TAG);
        Element ulElement = ulElements.first();
        if (ulElement == null) {
            Elements linkElements = liElement.getElementsByTag(A_TAG);

            return !linkElements.isEmpty() ? linkElements : new Elements();
        } else {
            Elements linkElements = new Elements();
            for (Element element : ulElement.getElementsByTag(LI_TAG)) {
                linkElements.addAll(resolveLinkElements(element));
            }
            return linkElements;
        }
    }
}
