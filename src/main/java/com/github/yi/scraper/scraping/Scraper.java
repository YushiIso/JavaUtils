package com.github.yi.scraper.scraping;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.yi.scraper.dto.Scraping;
import com.github.yi.scraper.dto.ScrapingDef;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Scraper {

	/**
	 * 定義にのっとりスクレイピングを行います<br>
	 * 指定されたcss
	 * selecterに問題があった場合、selecterに何もヒットしなかった場合、正規表現がおかしい場合は結果にnullが入ります。
	 *
	 * @param baseUri
	 *            url
	 * @param in
	 *            htmlファイルのinputstreame
	 * @param charsetName
	 *            htmlファイルの文字コード。nullの場合はUTF-8で読み込みます。
	 * @param scrapingDef
	 *            スクレイピング定義
	 * @return スクレイピング結果を入れたリスト
	 * @throws IOException
	 */
	public static List<List<Scraping>> scraping(final String baseUri, final InputStream in, final String charsetName,
			final ScrapingDef scrapingDef) throws IOException {
		final Document doc = Jsoup.parse(in, charsetName, baseUri);
		return scraping(doc, scrapingDef);
	}

	/**
	 * 定義にのっとりスクレイピングを行います<br>
	 * 指定されたcss selecterに問題があった場合、selecterに何もヒットしなかった場合、正規表現がおかしい場合は結果にnullが入ります
	 *
	 * @param doc
	 *            jsoupのドキュメントオブジェクト
	 * @param scrapingDef
	 *            スクレイピング定義
	 * @return スクレイピング結果を入れたリスト
	 */
	public static List<List<Scraping>> scraping(final Document doc, final ScrapingDef scrapingDef) {
		log.info("scraping target url={}", doc.baseUri());
		if (scrapingDef.getListDef() != null) {
			if (isURLPatternMatch(doc.baseUri(), scrapingDef.getListDef().getUrlPattern())) {
				return listScraping(doc, scrapingDef);
			}
		}

		final List<List<Scraping>> result = new ArrayList<>();
		result.add(scraping(doc, null, scrapingDef.getScrapingDefs()));
		return result;
	}

	private static List<List<Scraping>> listScraping(final Document doc, final ScrapingDef scrapingDef) {
		final Elements eles = doc.select(scrapingDef.getListDef().getListSelector());
		Element header = null;
		if (scrapingDef.getHeaderDef() != null) {
			header = doc.select(scrapingDef.getHeaderDef()).first();
		}
		final List<List<Scraping>> result = new ArrayList<>();

		for (final Element ele : eles) {
			final Document table = Jsoup.parse("");
			if (header != null) {
				table.select("body").first().appendChild(header);
			}
			table.select("body").first().appendChild(ele);

			result.add(scraping(doc, table, scrapingDef.getListDef().getScrapingDefs()));
		}

		return result;
	}

	private static List<Scraping> scraping(final Document doc, final Element ele, final List<Scraping> list) {
		final List<Scraping> data = new ArrayList<>();
		for (final Scraping def : list) {
			if (def.getSrc() == null) {
				log.error("src is null : selecter={}", def.getSelector());
				def.setScrapingData(null);
				data.add(def);
				continue;
			}

			switch (def.getSrc()) {
			case "html":
				data.add(html(doc, def));
				break;
			case "url":
				data.add(url(doc, def));
				break;
			case "list":
				data.add(html(ele, def));
				break;
			case "dummy":
				data.add(dummy(def));
				break;
			default:
				log.warn("not src : {}", def.getSrc());
				def.setScrapingData(null);
				data.add(def);
				break;
			}
		}

		return data;
	}

	private static Scraping dummy(final Scraping def) {
		def.setScrapingData("dummy");
		return def;
	}

	private static Scraping url(final Element ele, final Scraping def) {
		return normalization(ele.baseUri(), def);
	}

	private static Scraping html(final Element ele, final Scraping def) {
		final Scraping d = new Scraping(def);
		try {
			final Elements sDoc = ele.select(d.getSelector());
			if (d.isRaw()) {
				if (sDoc.toString().isEmpty()) {
					d.setScrapingData(null);
				} else {
					d.setScrapingData(sDoc.toString());
				}
				return d;
			}
			return normalization(sDoc.text(), d);
		} catch (final Exception e) {
			log.warn("css selector syntax error : {}", d.getSelector(), e);
			return normalization("", d);
		}
	}

	private static Scraping normalization(final String str, final Scraping def) throws PatternSyntaxException {
		String buf = str;
		if (def.getCutRegexp() != null) {
			try {
				buf = normalization(str, def.getCutRegexp());
			} catch (final PatternSyntaxException e) {
				log.warn("regex syntax error : {}", def.getCutRegexp(), e);
			}
		}

		if (buf.isEmpty()) {
			buf = null;
		}
		def.setScrapingData(buf);

		return def;
	}

	private static String normalization(final String str, final String regex) throws PatternSyntaxException {
		final Pattern p = Pattern.compile(regex);
		final Matcher m = p.matcher(str);

		if (m.find()) {
			return m.group();
		}
		return "";
	}

	private static boolean isURLPatternMatch(final String url, final String regex) {
		return normalization(url, regex) != "";
	}

}
