## Scraper for products from different shops
 
Table of contents
---
- [General info](#general-info)
- [How it works](#how-it-works)
- [Other information](#other-information)
<br/>

General info 
---
**Shop compare** is a web application that provides prices comparison for different appliances and electronic 
devices.
The main goal of this application is to enable customers find their required product at the best price available at the 
market. To achieve the implementation of this application, this Scraper application service is used for scraping
categories and products within different shops.

Scraper app presents an application for scraping categories and products out of a few shop websites
that sell variety of appliances and electronic devices. The products data is being fetched by scraping the websites
of the most popular markets in Macedonia for appliances, white goods and other electronic devices.

How it works
---
Before diving deep into the logic of scraping products, it's essential to have determined the categories of those 
products.
In order to perform scraping of the products from the shop website, we need to traverse throughout all categories, 
gather information about the categories such as category name, category products URL and scrape the products from the 
URL of each category.

In the class [ScrapingProcessor](scraper-business/src/main/java/com/shopcompare/scraper/scheduled_jobs/ScrapingProcessor.java)
we have defined the scheduled jobs per each shop and the cron expression for each 
shop is set to execute the job once daily at predefined hour. 
The procedure of scraping data of each shop is the following:
1. We scrape the categories pages URLs along with category name within the shop website.
2. In order to maintain using the same category names within the system, a call to [category-mapper service](https://github.com/stefanikostic/category-mapper) is performed in order to map the fetched category names to the 
   already predefined category names from configuration. 
   More details about the predefined categories can be found in the docs of [category-mapper service](https://github.com/stefanikostic/category-mapper).
3. By listing all mapped categories, we are scraping the products using the category website URL.
   3.1. When all products of a category are fetched, we sent the result list of products to RabbitMQ queue named 
   `scraped_products`.


Other information
---
##### scraper-category-scraping
About the process of **scraping category links**, the definitions of the methods used can be found in class 
[CategoryLinksFetcher](scraper-category-scraping/src/main/java/com/shopcompare/scraper/category/scraping/CategoryLinksFetcher.java).
Since different shops have different structure of their website contents, we have implementation of CategoryLinksFetcher
per shop. The categories data is static in all the shop websites, therefore we use Jsoup in order to fetch the data 
from the HTML elements.

The category mapper url is also configured via the property `category-mapper.url` based on the environment.

##### scraper-business
The structure of the code responsible for scraping the products is similar to the code of scraping the categories.
All methods definitions required for scraping the products are present in the class 
[ScrapingProductsService](scraper-business/src/main/java/com/shopcompare/scraper/product/scraping/ScrapingProductsService.java).
As the shop website contents differ, we have a dedicated implementation of ScrapingProductsService per shop. <br/>

We are using different tools for scraping the data from the shop website. In some of the scenarios where the 
products are static HTML content, we utilize Jsoup in order to get the products data out of the HTML elements.
Otherwise, in the scenarios where the products are dynamic content, Jsoup cannot perform the scraping since the 
content is loaded through JavaScript. For those cases, we use Selenium Chrome web driver to automate the web browser
and wait for the products results until the page is loaded.
