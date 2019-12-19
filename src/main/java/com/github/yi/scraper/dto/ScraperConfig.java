package com.github.yi.scraper.dto;

import org.constretto.annotation.Configuration;

import lombok.Getter;

public class ScraperConfig {
	@Getter
	@Configuration("scraper.finishedEndpoint")
	private String finishedEndpoint;

	@Getter
	@Configuration("scraper.scrapingKey")
	private String scraperKey;

	@Getter
	@Configuration("scraper.normalizeKey")
	private String normalizeKey;

}
