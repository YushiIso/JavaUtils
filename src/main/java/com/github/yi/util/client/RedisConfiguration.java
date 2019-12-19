package com.github.yi.util.client;

import org.constretto.annotation.Configuration;

import lombok.Data;

@Data
public class RedisConfiguration {
	@Configuration
	private String endpoint;

	@Configuration
	private int port;
}
