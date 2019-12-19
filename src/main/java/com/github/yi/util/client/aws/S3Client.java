package com.github.yi.util.client.aws;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.github.yi.util.client.aws.config.S3Config;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class S3Client extends AWSClient {
	private final String bucketName;
	private final AmazonS3 client;

	public S3Client(final S3Config config) {
		this(config.getAccessKey(), config.getSecretKey(), config.getBucketName(), config.getS3EndpointURL(),
				config.getRegion(), config.getRetryMax());
	}

	/**
	 * @param accessKey
	 *            IAMアクセスキー
	 * @param secretKey
	 *            IAMシークレットキー
	 * @param bucketName
	 *            操作対象バケット
	 * @param endpointURL
	 *            S3のエンドポイント
	 * @param region
	 *            署名情報をリクエストするリージョン
	 * @param retryMax
	 *            リトライ回数
	 */
	public S3Client(final String accessKey, final String secretKey, final String bucketName, final String endpointURL,
			final String region, final int retryMax) {
		log.debug("S3 ready\taccess_key:{}\tsecret_key:{}\tbucket_name:{}\tendopint:{}\tregion:{}", accessKey,
				secretKey, bucketName, endpointURL, region);
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.bucketName = bucketName;
		this.endpointURL = endpointURL;
		this.region = region;
		this.retryMax = retryMax;
		this.client = this.build();
	}

	private AmazonS3 build() {
		log.debug("build S3 Client");
		final AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();

		final AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
		builder.withCredentials(new AWSStaticCredentialsProvider(credentials));

		final ClientConfiguration clientConfig = new ClientConfiguration();
		clientConfig.setConnectionTimeout((int) TimeUnit.SECONDS.toMillis(10));
		clientConfig.setMaxErrorRetry(10);
		builder.withClientConfiguration(clientConfig);

		final EndpointConfiguration endpointConfig = new EndpointConfiguration(this.endpointURL, this.region);
		builder.withEndpointConfiguration(endpointConfig);

		return builder.build();
	}

	/**
	 * S3のバケットにファイルをアップロードする
	 *
	 * @param localfile
	 *            アップロードしたいファイル
	 * @param s3path
	 *            アップロード先のパス
	 * @throws AwsException
	 * @throws InterruptedException
	 */
	public void put(final File localfile, final String s3path) throws AwsException, InterruptedException {
		if (this.client == null) {
			throw new AwsException(String.format("Failed to build client; access-key=%s, secret-key=%s", this.accessKey,
					this.secretKey));
		}
		log.debug("put object from:{} to:{}", localfile, s3path);
		for (int i = 0; i < this.retryMax; i++) {
			try {
				this.client.putObject(this.bucketName, s3path, localfile);
				break;
			} catch (AmazonClientException | IllegalArgumentException e) {
				if (i == this.retryMax - 1) {
					throw new AwsException(e);
				}
				log.warn("Failed to put object", e);
				TimeUnit.SECONDS.sleep(10);
			}
		}
	}

	private S3Object getS3Obj(String bucket, final String s3path) throws AwsException, InterruptedException {
		for (int i = 0; i < this.retryMax; i++) {
			try {
				return this.client.getObject(bucket, s3path);
			} catch (AmazonClientException | IllegalArgumentException e) {
				log.warn("Failed to get s3object", e);
				TimeUnit.SECONDS.sleep(10);
			}
		}
		throw new AwsException("Failed to get s3object");
	}

	public void get(final String s3path, final File localfile) throws AwsException, IOException, InterruptedException {
		log.debug("get object\tpath:{}\treport-file:{}", s3path, localfile);
		try (S3Object s3obj = this.getS3Obj(this.bucketName, s3path)) {
			Files.copy(s3obj.getObjectContent(), localfile.toPath());
		}
	}

	public void get(final String bucket, final String s3path, final File localfile)
			throws AwsException, IOException, InterruptedException {
		log.debug("get object\tpath:{}\treport-file:{}", s3path, localfile);
		try (S3Object s3obj = this.getS3Obj(bucket, s3path)) {
			Files.copy(s3obj.getObjectContent(), localfile.toPath());
		}
	}

	public InputStream get(final String s3path) throws AwsException, InterruptedException {
		log.info("get input streamm : {}", s3path);
		return this.getS3Obj(this.bucketName, s3path).getObjectContent();
	}

	public InputStream get(final String bucket, final String s3path) throws AwsException, InterruptedException {
		log.debug("get input streamm : {}", s3path);
		return this.getS3Obj(bucket, s3path).getObjectContent();
	}

	public List<String> getKeyList(final String prefix) throws AwsException {
		return this.getKeyList(this.bucketName, prefix);
	}

	public List<String> getKeyList(final String bucket, final String prefix) throws AwsException {
		log.debug("get key list from : bucket={} prefix={}", bucket, prefix);
		final List<String> list = new ArrayList<>();
		try {
			this.client.listObjects(bucket, prefix).getObjectSummaries().forEach(s3obj -> {
				list.add(s3obj.getKey());
			});
		} catch (final Exception e) {
			throw new AwsException(e);
		}

		return list;
	}

	public boolean rename(final String fromKey, final String toKey) throws AwsException {
		return this.rename(this.bucketName, fromKey, this.bucketName, toKey);
	}

	public boolean rename(final String fromBucket, final String fromKey, final String toBucket, final String toKey)
			throws AwsException {
		try {
			final CopyObjectResult res = this.client.copyObject(fromBucket, fromKey, toBucket, toKey);
			if (res == null) {
				log.error("faild rename :form:bucket={} key={}  to:bucket={} key={}", fromBucket, fromKey, toBucket,
						toKey);
				return Boolean.FALSE;
			}

			this.client.deleteObject(fromBucket, fromKey);
			log.debug("rename form:bucket={} key={}  to:bucket={} key={}", fromBucket, fromKey, toBucket, toKey);
		} catch (final Exception e) {
			throw new AwsException(e);
		}
		return Boolean.TRUE;
	}
}
