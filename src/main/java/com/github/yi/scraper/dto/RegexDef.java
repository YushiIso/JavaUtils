package com.github.yi.scraper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

public class RegexDef {
	/*
	 * 定義のユニークid
	 */
	@Getter
	@JsonProperty("id")
	private int id;
	/*
	 * 定義の概要、説明
	 */
	@Getter
	@JsonProperty("desc")
	private String desc;
	/*
	 * 正規化処理の種類。replace,scriptのどちらか
	 */
	@Getter
	@JsonProperty("method")
	private String method;

	/*
	 * 置換対象を示す正規表現
	 */
	@Getter
	@JsonProperty("regex")
	private String regex;
	/*
	 * 置換後の文字列
	 */
	@Getter
	@JsonProperty("replacement")
	private String replacement;

	/*
	 * javascriptでの正規化処理
	 */
	@Getter
	@JsonProperty("script")
	private String script;
	/*
	 * 正規化処理関数名
	 */
	@Getter
	@JsonProperty("scriptfunc")
	private String scriptFunc;

}
