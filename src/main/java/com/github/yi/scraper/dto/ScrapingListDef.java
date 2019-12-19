package com.github.yi.scraper.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

public class ScrapingListDef {
	/*
	 * listを取得するページを判定するためのurlの正規表現
	 */
	@Getter
	@JsonProperty("urlpattern")
	private String urlPattern;
	/*
	 * listを取得するcss selecter
	 */
	@Getter
	@JsonProperty("listselector")
	private String listSelector;
	/*
	 * スクレイピング定義の配列
	 */
	@Getter
	@JsonProperty("scrapingdef")
	private List<Scraping> scrapingDefs;
}
