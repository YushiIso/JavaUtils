package com.github.yi.util.client.aws.config;

import org.constretto.annotation.Configuration;

import lombok.Getter;

@Getter
public class AWSConfig {
	/** IAMアクセスキー */
	@Configuration("accessKey")
	private String accessKey;

	/** IAMシークレットキー */
	@Configuration("secretKey")
	private String secretKey;

	/** リトライ回数 */
	@Configuration("retryMax")
	private int retryMax;
}
