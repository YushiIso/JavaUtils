package com.github.yi.crawler;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;

import org.jsoup.Connection.KeyVal;
import org.jsoup.nodes.Document;

/**
 * @author YushiIso
 */
public interface HttpClient extends Closeable {
	/**
	 * url先に接続してhtmlを取得します
	 *
	 * @param url
	 *            接続先
	 * @return 接続先のhtml Document
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Document get(String url) throws IOException, InterruptedException;

	/**
	 * url先にpostDataをpostします。<br>
	 * postDataはkey=value;でつなげてpostします。
	 *
	 * @param url
	 *            接続先
	 * @param postData
	 *            postするkey-valueのmap
	 * @return postした結果のDocument
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Document post(String url, Collection<KeyVal> postData) throws IOException, InterruptedException;
}
