package com.shopcompare.scraper.category.scraping;

/**
 * POJO for storing category details.
 *
 * @param url link of products that belong to the category
 * @param categoryName name of the category
 * @param shopName name of the shop
 */
public record CategoryLink(String url, String categoryName, String shopName) {

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CategoryLink objCategoryLink) {
            return this.url.equals(objCategoryLink.url);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
