package com.github.yi.util.client;

import java.io.Closeable;
import java.util.Set;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

@Slf4j
public class RedisClient implements Closeable {
	private final int DEFAULT_TIMEOUT = 10000;
	@Getter
	private final Jedis redis;

	public RedisClient(final RedisConfiguration config) {
		log.debug("redis config endpoint={}, port={}", config.getEndpoint(), config.getPort());
		this.redis = new Jedis(config.getEndpoint(), config.getPort(), this.DEFAULT_TIMEOUT);
	}

	public String get(final String code) {
		return this.redis.get(code);
	}

	public void set(final String code, final String json) {
		this.redis.set(code, json);
	}

	public void del(final String code) {
		this.redis.del(code);
	}

	public ScanResult<String> getKeys(final int limit, final int cursor) {
		final ScanParams params = new ScanParams();
		params.count(limit);
		params.match("*");

		return this.redis.scan(String.valueOf(cursor), params);
	}

	public Set<String> getAllKeys() {
		return this.redis.keys("*");
	}

	@Override
	public void close() {
		this.redis.disconnect();
	}
}
