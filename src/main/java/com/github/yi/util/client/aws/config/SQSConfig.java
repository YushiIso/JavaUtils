package com.github.yi.util.client.aws.config;

import org.constretto.annotation.Configuration;

import lombok.Getter;

@Getter
public class SQSConfig extends AWSConfig {
	/**
	 * 利用するSQSのエンドポイント<br>
	 * SQSの実際のURLではない
	 */
	@Configuration("sqs.endpointURL")
	private String sqsEndpointURL;

	/** 署名情報をリクエストするリージョン */
	@Configuration("sqs.region")
	private String region;

	/** 使用するSQSのURL */
	@Configuration("sqs.url")
	private String url;
}
