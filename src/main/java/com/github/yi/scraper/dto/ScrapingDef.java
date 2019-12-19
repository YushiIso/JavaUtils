package com.github.yi.scraper.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

public class ScrapingDef {
	/*
	 * サイトID
	 */
	@Getter
	@JsonProperty("siteid")
	private int siteID;
	/*
	 * カテゴリID
	 */
	@Getter
	@JsonProperty("categoryid")
	private int categoryID;
	/*
	 * スクレイピング定義の配列
	 */
	@Getter
	@JsonProperty("scrapingdef")
	private List<Scraping> scrapingDefs;

	/*
	 * スクレイピング定義の配列
	 */
	@Getter
	@JsonProperty("listdef")
	private ScrapingListDef listDef;

	/*
	 * 表のヘッダのselector
	 */
	@Getter
	@JsonProperty("headerdef")
	private String headerDef;

}
