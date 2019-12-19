package com.github.yi.crawler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.Collection;

import org.jsoup.Connection;
import org.jsoup.Connection.KeyVal;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsoupBaseCrawler extends HttpClientTimeUtil implements HttpClient {
	@Getter
	@Setter
	private String userAgent;
	@Getter
	@Setter
	private Proxy proxy;

	/**
	 *
	 */
	public JsoupBaseCrawler() {
		//
	}

	public JsoupBaseCrawler(String userAgent) {
		this.userAgent = userAgent;
	}

	public JsoupBaseCrawler(final Proxy proxy) {
		this.proxy = proxy;
	}

	public JsoupBaseCrawler(final Type type, final String proxyHost, final int proxyPort) {
		this.proxy = new Proxy(type, new InetSocketAddress(proxyHost, proxyPort));
	}

	public JsoupBaseCrawler(final Type type, final String proxyHost, final int proxyPort, String userAgent) {
		this(new Proxy(type, new InetSocketAddress(proxyHost, proxyPort)), userAgent);
	}

	public JsoupBaseCrawler(final Proxy proxy, String userAgent) {
		this.userAgent = userAgent;
		this.proxy = proxy;
	}

	@Override
	public Document get(final String url) throws IOException, InterruptedException {
		log.debug("connect get : {}", url);
		final Document doc = this.createConn(url).get();
		this.sleep();
		return doc;
	}

	@Override
	public Document post(final String url, final Collection<KeyVal> postData) throws IOException, InterruptedException {
		log.debug("connect post : {}", url);
		final Document doc = this.createConn(url).data(postData).post();
		this.sleep();
		return doc;
	}

	private Connection createConn(String url) {
		Connection conn = Jsoup.connect(url);
		conn = this.userAgent == null ? conn : conn.userAgent(this.userAgent);
		conn = this.proxy == null ? conn : conn.proxy(this.proxy);
		return conn.timeout((int) this.getTimeout());
	}

	@Override
	public void close() throws IOException {
		//
	}
}
