package com.github.yi.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CashingFileReader {
	protected LoadingCache<String, String> cache;

	@Getter
	@Setter
	private int maximumSize;
	@Getter
	@Setter
	private int afterTime;
	@Getter
	@Setter
	private TimeUnit afterTimeUnit;

	public CashingFileReader() {
		this(20, 5, TimeUnit.MINUTES);
	}

	public CashingFileReader(int maximumSize, int afterTime, TimeUnit afterTimeUnit) {
		this.maximumSize = maximumSize;
		this.afterTime = afterTime;
		this.afterTimeUnit = afterTimeUnit;
		this.setCache(new Loader());
	}

	protected void setCache(CacheLoader<? super String, String> loader) {
		this.cache = CacheBuilder
				.newBuilder()
				.maximumSize(this.maximumSize)
				.softValues()
				.expireAfterAccess(this.afterTime, this.afterTimeUnit)
				.build(loader);
	}

	public String getDefinitionFile(final String key) throws ExecutionException {
		log.info("get file : {}", key);
		try {
			return this.cache.get(key);
		} catch (final ExecutionException e) {
			log.warn("Filed get definition file :{}", key, e);
			throw e;
		}
	}

	protected class Loader extends CacheLoader<String, String> {
		public Loader() {
			//
		}

		@Override
		public String load(final String key) throws Exception {
			final InputStream in = Files.newInputStream(new File(key).toPath(), StandardOpenOption.READ);
			return this.convert(in);
		}

		protected String convert(final InputStream in) throws IOException {
			final InputStreamReader reader = new InputStreamReader(in);
			final StringBuilder builder = new StringBuilder();
			final char[] buffer = new char[512];
			int read;
			while (0 <= (read = reader.read(buffer))) {
				builder.append(buffer, 0, read);
			}
			return builder.toString();
		}
	}

}
