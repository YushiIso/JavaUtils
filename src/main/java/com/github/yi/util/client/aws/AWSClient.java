package com.github.yi.util.client.aws;

public class AWSClient {
	/** IAMアクセスキー */
	protected String accessKey;

	/** IAMシークレットキー */
	protected String secretKey;

	/** サービスのエンドポイント */
	protected String endpointURL;

	/** 署名情報をリクエストするリージョン */
	protected String region;

	/** 失敗した時にリトライする回数 */
	protected int retryMax;

}
