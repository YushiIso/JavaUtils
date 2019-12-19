package com.github.yi.scraper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class Scraping {
	/**
	 * データを取得する対象ソース html,url
	 */
	@Getter
	@JsonProperty("src")
	private String src;
	/**
	 * スクレイピングする項目名
	 */
	@Getter
	@JsonProperty("valname")
	private String valName;
	/**
	 * css selector
	 */
	@Getter
	@JsonProperty("selector")
	private String selector;
	/**
	 * スクレイピング結果をHTMLで持つかどうか
	 */
	@Getter
	@JsonProperty("raw")
	private boolean raw;
	/**
	 * 取れるデータのtype(integer,float,stringなど)
	 */
	@Getter
	@JsonProperty("type")
	private String type;
	/**
	 * スクレイピングしてきたものから必要なもの部分を抜き出す必要がある場合の正規表現
	 */
	@Getter
	@JsonProperty("cutregexp")
	private String cutRegexp;
	/**
	 * 抜き出したデータが期待されているものかフォーマットでのチェックする正規表現
	 */
	@Getter
	@JsonProperty("formatregexp")
	private String formatRegexp;
	/**
	 * 正規化に使用する正規化定義IDの配列
	 */
	@Getter
	@JsonProperty("regdefid")
	private int[] regDefID;

	/**
	 * スクレイピングした結果
	 */
	@Getter
	@Setter
	private String scrapingData;

	public Scraping() {
		//
	}

	public Scraping(final Scraping def) {
		this.src = def.getSrc();
		this.valName = def.getValName();
		this.selector = def.getSelector();
		this.raw = def.isRaw();
		this.type = def.getType();
		this.cutRegexp = def.getCutRegexp();
		this.formatRegexp = def.getFormatRegexp();
		this.regDefID = def.getRegDefID();
	}

}
