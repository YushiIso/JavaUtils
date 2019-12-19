package com.github.yi.scraper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.script.ScriptException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yi.crawler.HttpClient;
import com.github.yi.crawler.JsoupBaseCrawler;
import com.github.yi.scraper.dto.RegexDef;
import com.github.yi.scraper.dto.ScraperConfig;
import com.github.yi.scraper.dto.Scraping;
import com.github.yi.scraper.dto.ScrapingDef;
import com.github.yi.scraper.scraping.Normalizer;
import com.github.yi.scraper.scraping.Scraper;
import com.github.yi.util.CashingFileReader;
import com.github.yi.util.ConfigReader;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * スクレイピングのメイン処理
 *
 * @author YushiIso
 */
@Slf4j
@Getter
public class ScraperRunner implements Cloneable {
	private final ScraperConfig config;

	private final CashingFileReader defClient;
	private final HttpClient httpClient;

	private final SimpleDateFormat format;

	@Setter
	private boolean running;

	/**
	 * 指定されたモードの設定を読んで実行する
	 *
	 * @param option
	 *            production,staging,develop
	 * @throws FileNotFoundException
	 */
	public ScraperRunner(final String option) throws FileNotFoundException {
		final String scf = String.format("scraper.%s.properties", option);
		if (!ConfigReader.configExits(scf)) {
			throw new FileNotFoundException(String.format("%sがありません", scf));
		}
		final String rcf = String.format("redis.%s.properties", option);
		if (!ConfigReader.configExits(scf)) {
			throw new FileNotFoundException(String.format("%sがありません", rcf));
		}
		this.config = ConfigReader.readConfig(scf, ScraperConfig.class);

		this.defClient = new CashingFileReader();
		this.httpClient = new JsoupBaseCrawler();

		this.format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		this.running = Boolean.TRUE;

	}

	public List<List<Scraping>> scraping(Path targetPath, Path defPath)
			throws IOException, ExecutionException {
		log.info("scraping start : file={}", targetPath.getFileName());
		final List<List<Scraping>> result = new ArrayList<>();

		final ScrapingDef sDef = this.getScrapingDef(defPath);
		final InputStream html = Files.newInputStream(targetPath, StandardOpenOption.READ);
		final List<List<Scraping>> list = Scraper.scraping("", html, null, sDef);

		list.forEach(data -> {
			try {
				result.add(this.normalize(data));
			} catch (final IOException e) {
				e.printStackTrace();
			} catch (final ExecutionException e) {
				e.printStackTrace();
			}
		});

		return result;
	}

	private ScrapingDef getScrapingDef(final Path defPath) throws IOException, ExecutionException {
		return new ObjectMapper().readValue(this.defClient.getDefinitionFile(defPath.toString()), ScrapingDef.class);
	}

	private List<Scraping> normalize(final List<Scraping> list) throws IOException, ExecutionException {
		final List<Scraping> results = new ArrayList<>();
		for (final Scraping scraping : list) {
			try {
				results.add(this.normalize(scraping));
			} catch (NoSuchMethodException | ScriptException e) {
				log.error("can't nomalize : {}", scraping.getValName());
			}
		}
		return results;
	}

	private Scraping normalize(final Scraping scraping)
			throws IOException, NoSuchMethodException, ScriptException, ExecutionException {
		if (scraping.getRegDefID() == null) {
			return scraping;
		}

		String str = scraping.getScrapingData();
		for (final int defID : scraping.getRegDefID()) {
			final String key = String.format("%s/%s.json", this.config.getNormalizeKey(), defID);
			final RegexDef def = new ObjectMapper().readValue(this.defClient.getDefinitionFile(key), RegexDef.class);
			str = Normalizer.normalize(str, def);
		}
		scraping.setScrapingData(str);
		return scraping;
	}

	public void close() {
		try {
			this.httpClient.close();
		} catch (final IOException e) {
			log.error("can't close client.", e);
		}
	}

	public static void main(final String[] args) throws FileNotFoundException {
		if (args.length != 1) {
			log.warn("実行する環境を引数に１つ指定してください。production , staging , develop");
			System.exit(0);
		}

		if (!"production , staging , develop".contains(args[0])) {
			log.warn("実行でき環境は以下の3つです。production , staging , develop");
			System.exit(0);
		}
		final ScraperRunner scraper = new ScraperRunner(args[0]);

	}
}
