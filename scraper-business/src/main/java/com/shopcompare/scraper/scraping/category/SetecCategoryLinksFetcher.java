package com.shopcompare.scraper.scraping.category;

import com.shopcompare.scraper.scraping.exception.FailedScrapingException;
import com.shopcompare.scraper.scraping.model.CategoryLink;
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

    @Override
    public Set<CategoryLink> fetchCategoriesLinks(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            Element menuWrapper = document.getElementsByClass("megamenu-wrapper").first();
            Set<CategoryLink> categoryLinks = new HashSet<>();
            if (menuWrapper != null) {
                Elements menuElements = menuWrapper.getElementsByClass("menu");
                Elements allListElements = new Elements();
                for (Element menuElement : menuElements) {
                    Elements listElements = menuElement.getElementsByTag(LI_TAG);
                    allListElements.addAll(listElements);
                }

                Elements linkElements = new Elements();
                for (Element liElement : allListElements) {
                    linkElements.addAll(resolveListElements(liElement));
                }

                for (Element linkElement : linkElements) {
                    CategoryLink categoryLink = new CategoryLink(linkElement.attr("href"), linkElement.text(),
                            SETEC);
                    categoryLinks.add(categoryLink);
                }
            }

            return categoryLinks;
        } catch (Exception e) {
            log.error("Invalid url: {}. Failed scraping categories for shop {}", url, SETEC);
            throw new FailedScrapingException(SETEC);
        }
    }

    private Elements resolveListElements(Element liElement) {
        Elements liElements = liElement.getElementsByTag(LI_TAG);
        if (liElements.isEmpty() && !liElement.getElementsByTag(A_TAG).isEmpty()) {
            return liElement.getElementsByTag(A_TAG);
        } else {
            Elements linkElements = new Elements();
            for (Element element : liElements) {
                linkElements.addAll(resolveListElements(element));
            }
            return linkElements;
        }
    }
}
