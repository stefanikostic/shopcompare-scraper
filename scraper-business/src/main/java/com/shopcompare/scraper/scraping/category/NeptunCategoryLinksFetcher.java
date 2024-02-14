package com.shopcompare.scraper.scraping.category;

import com.shopcompare.scraper.scraping.exception.FailedScrapingException;
import com.shopcompare.scraper.scraping.model.CategoryLink;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Implementation of {@link CategoryLinksFetcher} for shop Neptun.
 */
@Slf4j
@Service
public class NeptunCategoryLinksFetcher implements CategoryLinksFetcher {

    private static final String NEPTUN = "Neptun";
    private static final String LI_TAG = "li";
    private static final String A_TAG = "a";
    private static final String UL_TAG = "ul";
    private static final String IMG_TAG = "img";
    private static final String NEPTUN_MAIN_ID = "neptunMain";
    private static final String HREF_ATTRIBUTE = "href";
    private static final String NEPTUN_MAIN_URL = "https://www.neptun.mk";


    @Override
    public Set<CategoryLink> fetchCategoriesLinks(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            Element menuWrapper = document.getElementById(NEPTUN_MAIN_ID);
            Element ulElement = menuWrapper != null ? menuWrapper.getElementsByTag(UL_TAG).first() : null;
            Set<CategoryLink> categoryLinks = new HashSet<>();
            if (ulElement != null) {
                Elements allListElements = ulElement.getElementsByTag(LI_TAG);

                for (Element liElement : allListElements) {
                    List<CategoryLink> categoryLinksFromLiElement = resolveCategoryLinksOutOfLinkElements(liElement,
                            null);
                    categoryLinks.addAll(categoryLinksFromLiElement);
                }
            }

            return categoryLinks;
        } catch (Exception e) {
            log.error("Invalid url: {}. Failed scraping categories for shop {}", url, NEPTUN);
            throw new FailedScrapingException(NEPTUN);
        }
    }

    private List<CategoryLink> resolveCategoryLinksOutOfLinkElements(Element liElement, String parentCategory) {
        Elements ulElements = liElement.getElementsByTag(UL_TAG);
        Element ulElement = ulElements.first();
        boolean liElementWithoutNestedList = ulElement == null;
        List<CategoryLink> categoryLinks = new ArrayList<>();

        Element linkElement = liElement.getElementsByTag(A_TAG).first();
        boolean liNotContainImage = liElement.getElementsByTag(IMG_TAG).isEmpty();
        CategoryLink categoryLink = buildCategoryLink(parentCategory, liNotContainImage, linkElement);
        if (categoryLink != null) {
            categoryLinks.add(categoryLink);
        }

        if (!liElementWithoutNestedList) {
            Elements nestedLiElements = ulElement.getElementsByTag(LI_TAG);
            for (Element element : nestedLiElements) {
                String superCategory = linkElement != null ? linkElement.text() : null;
                List<CategoryLink> linkElements = resolveCategoryLinksOutOfLinkElements(element, superCategory);
                categoryLinks.addAll(linkElements);
            }
        }
        return categoryLinks;
    }

    private CategoryLink buildCategoryLink(String parentCategory, boolean liNotContainNestedImage,
                                                 Element linkElement) {
        if (liNotContainNestedImage && linkElement != null) {
            String categoryName = parentCategory != null ? String.format("%s %s", parentCategory, linkElement.text())
                    : linkElement.text();

            return new CategoryLink(NEPTUN_MAIN_URL + linkElement.attr(HREF_ATTRIBUTE),
                    categoryName, NEPTUN);
        }
        return null;
    }
}
