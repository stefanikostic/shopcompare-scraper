package com.shopcompare.scraper;

import com.shopcompare.scraper.rabbitmq.ScaperRabbitMqMarker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackageClasses = {ScraperMarker.class,
		ScaperRabbitMqMarker.class})
public class ScraperApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScraperApplication.class, args);
	}

}
