package com.github.yi.scraper.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yi.util.CashingFileReader;
import com.github.yi.util.client.aws.S3Client;
import com.github.yi.util.client.aws.config.S3Config;

import lombok.extern.slf4j.Slf4j;

/**
 * @author YushiIso
 */
@Slf4j
public class DefinitionFileS3Client extends CashingFileReader {
	private final ObjectMapper mapper;

	public DefinitionFileS3Client(final S3Config config) {
		this(new S3Client(config));
	}

	public DefinitionFileS3Client(final S3Client s3) {
		super();
		this.mapper = new ObjectMapper();

		this.setCache(new Loader() {
			@Override
			public String load(final String key) throws Exception {
				final InputStream in = s3.get(key);
				return this.convert(in);
			}
		});
	}

	/**
	 * 定義ファイルをS3から取ってくる。
	 *
	 * @param key
	 *            定義ファイルにkey
	 * @param cla
	 *            定義ファイルのフォーマットに合わせたclass
	 * @return 定義ファイルのjavaオブジェクト。取得に失敗したらnullを返す
	 * @throws IOException
	 * @throws ExecutionException
	 */
	public <T> T getDefinitionFile(final String key, final Class<T> cla) throws IOException, ExecutionException {
		log.info("get definition file : {}", key);
		try {
			final String json = this.cache.get(key);
			return this.mapper.readValue(json, cla);
		} catch (IOException | ExecutionException e) {
			log.warn("Filed get definition file :{}", key, e);
			throw e;
		}
	}
}
