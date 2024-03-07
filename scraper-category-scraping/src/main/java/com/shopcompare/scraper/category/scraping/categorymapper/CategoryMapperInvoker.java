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

@Service
@RequiredArgsConstructor
public class CategoryMapperInvoker {

    private final RestTemplate restTemplate;

    public List<CategoryLink> formMappedCategories(Set<CategoryLink> categories) {
        Map<String, String> categoriesMap =
                restTemplate.postForObject("http://localhost:8089/mapCategories",
                        categories.stream().map(CategoryLink::categoryName).collect(Collectors.toSet()), Map.class);

        List<CategoryLink> mappedCategoryLinks = new ArrayList<>();
        for (CategoryLink categoryLink : categories) {
            if (categoriesMap != null && categoriesMap.get(categoryLink.categoryName()) != null) {
                CategoryLink mappedCategoryLink = new CategoryLink(categoryLink.url(),
                        categoriesMap.get(categoryLink.categoryName()),
                        categoryLink.shopName());
                mappedCategoryLinks.add(mappedCategoryLink);
            }
        }

        return mappedCategoryLinks;
    }

}
