package com.github.yi.crawler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class BaseCrawler implements Runnable {
	private Proxy proxy;

	public Document crawl(final String url) throws IOException {
		if (this.proxy != null) {
			Jsoup.connect(url).proxy(this.proxy).get();
		}

		return Jsoup.connect(url).get();
	}

	public static Elements select(final Document doc, final String selecter) {
		return doc.select(selecter);
	}

	public static void sleep(final TimeUnit unit, final long sleepTime) throws InterruptedException {
		Thread.sleep(unit.toMillis(sleepTime));
	}

	public BaseCrawler() {
		//
	}

	public BaseCrawler(final Type type, final String proxyAddr, final int proxyPort) {
		this.proxy = new Proxy(type, new InetSocketAddress(proxyAddr, proxyPort));
	}

	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ

	}
}
