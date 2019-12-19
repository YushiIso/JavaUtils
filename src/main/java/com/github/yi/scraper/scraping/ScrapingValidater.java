package com.github.yi.scraper.scraping;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.yi.scraper.dto.Scraping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScrapingValidater {

	/**
	 * strが定義に当てはまっている値かを検証する。<br>
	 * 定義に検証用のフォーマットがない場合は、trueを返す。
	 *
	 * @param str
	 *            検証対象の文字列
	 * @param data
	 *            定義
	 * @return 定義にそっていたらtrueを返す
	 */
	public static boolean validate(final String str, final Scraping data) {
		boolean result = true;
		log.info("start validating [{}] ", str);

		if (data.getFormatRegexp() != null) {
			result = result && validateRegex(str, data.getFormatRegexp());
		}

		if (data.getType() != null) {
			result = result && validateType(str, data.getType());
		}

		return result;
	}

	private static boolean validateRegex(final String target, final String regex) {
		log.info("validating by regex. str={} , regex={} ", target, regex);
		final Pattern p = Pattern.compile(regex);
		final Matcher m = p.matcher(target);

		if (!m.find()) {
			return Boolean.FALSE;
		}

		if (!m.group().equals(target)) {
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	private static boolean validateType(final String target, final String type) {
		log.info("validating by type. str={} , type={} ", target, type);
		switch (type) {
		case "integer":
			try {
				Integer.valueOf(target);
				return Boolean.TRUE;
			} catch (final Exception e) {
				log.warn("not integer :{}", target, e);
				return Boolean.FALSE;
			}
		case "long":
			try {
				Long.valueOf(target);
				return Boolean.TRUE;
			} catch (final Exception e) {
				log.warn("not long :{}", target, e);
				return Boolean.FALSE;
			}
		case "float":
			try {
				Float.valueOf(target);
				return Boolean.TRUE;
			} catch (final Exception e) {
				log.warn("not float :{}", target, e);
				return Boolean.FALSE;
			}
		case "string":
			if (target.isEmpty()) {
				return Boolean.FALSE;
			}
			return Boolean.TRUE;

		default:
			log.error("type[{}] is not find.", type);
			return Boolean.FALSE;
		}
	}

}
