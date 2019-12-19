package com.github.yi.crawler;

import java.io.IOException;
import java.util.Collection;

import org.jsoup.Connection.KeyVal;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.Proxy;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;

import lombok.extern.slf4j.Slf4j;

/**
 * @author YushiIso
 */
@Slf4j
public class SelenimuWrapperCrawler extends JsoupBaseCrawler {

	private boolean isInit = true;

	private void initConfiguration() {
		Configuration.browser = WebDriverRunner.CHROME;
		Configuration.headless = true;
		Configuration.browserSize = "1280x720";
		if (this.getProxy() != null) {
			WebDriverRunner.setProxy(new Proxy().setHttpProxy(this.getProxy().address().toString()));
		}

		this.isInit = false;
	}

	@Override
	public Document get(final String url) throws IOException {
		if (this.isInit) {
			this.initConfiguration();
		}

		log.info("connect get : {}", url);
		Selenide.open(url);
		final Document doc = Jsoup.parse(Selenide.$("html").innerHtml());
		doc.setBaseUri(url);
		return doc;
	}

	@Override
	public void close() throws IOException {
		Selenide.close();
	}

	/**
	 * 未実装
	 */
	@Override
	public Document post(String url, Collection<KeyVal> postData) throws IOException, InterruptedException {
		if (this.isInit) {
			this.initConfiguration();
		}
		return null;
	}

}
