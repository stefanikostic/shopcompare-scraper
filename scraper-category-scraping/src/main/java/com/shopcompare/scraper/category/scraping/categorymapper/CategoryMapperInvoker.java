package com.shopcompare.scraper.category.scraping.categorymapper;

import com.shopcompare.scraper.category.scraping.CategoryLink;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service that performs a REST call to category mapper service in order to map scraped categories into predefined ones.
 */
@Service
@RequiredArgsConstructor
public class CategoryMapperInvoker {

    private final RestTemplate restTemplate;
    private final CategoryMapperProperties categoryMapperProperties;

    /**
     * Calls category mapper service by providing the scraped categories in order to get the corresponding
     * predefined categories.
     * <p>The scraped categories can differ, therefore we have stored predefined categories in the system and mapping to
     * those is required to have same categories used in all applications within the system.</p>
     * <i>*Note: Mapped category is returned only if the scraped category is found in the configuration of the category
     * mapper service. Otherwise, the unknown category won't be mapped.
     * </i>
     *
     * @param categories scraped categories from different web shops.
     * @return list of mapped {@link CategoryLink}.
     */
    public List<CategoryLink> formMappedCategories(Set<CategoryLink> categories) {
        String categoryMapperUrl = categoryMapperProperties.getUrl();
        Map<String, String> categoriesMap =
                restTemplate.postForObject(categoryMapperUrl,
                        categories.stream().map(CategoryLink::categoryName).collect(Collectors.toSet()), Map.class);

        List<CategoryLink> mappedCategoryLinks = new ArrayList<>();
        if (categoriesMap != null) {
            for (CategoryLink categoryLink : categories) {
                if (categoriesMap.get(categoryLink.categoryName()) != null) {
                    CategoryLink mappedCategoryLink = new CategoryLink(categoryLink.url(),
                            categoriesMap.get(categoryLink.categoryName()),
                            categoryLink.shopName());
                    mappedCategoryLinks.add(mappedCategoryLink);
                }
            }
        }

        return mappedCategoryLinks;
    }

}
