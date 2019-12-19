package com.github.yi.util.client.aws.config;

import org.constretto.annotation.Configuration;

import lombok.Getter;

@Getter
public class S3Config extends AWSConfig {
	/** 操作対象バケット */
	@Configuration("s3.bucketName")
	private String bucketName;

	/** S3のエンドポイント */
	@Configuration("s3.endpointURL")
	private String s3EndpointURL;

	/** 署名情報をリクエストするリージョン */
	@Configuration("s3.region")
	private String region;

}
