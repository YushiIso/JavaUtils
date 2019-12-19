package com.github.yi.util.client.aws;

import java.util.concurrent.TimeUnit;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageResult;
import com.amazonaws.services.sqs.model.InvalidMessageContentsException;
import com.amazonaws.services.sqs.model.OverLimitException;
import com.amazonaws.services.sqs.model.ReceiptHandleIsInvalidException;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.github.yi.util.client.aws.config.SQSConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SQSClient extends AWSClient {

	private final AmazonSQS client;

	/**
	 * @param config
	 *            SQS利用に必要な情報
	 */
	public SQSClient(final SQSConfig config) {
		this(config.getAccessKey(), config.getSecretKey(), config.getSqsEndpointURL(), config.getRegion(),
				config.getRetryMax());
	}

	/**
	 * @param accessKey
	 *            IAMアクセスキー
	 * @param secretKey
	 *            IAMシークレットキー
	 * @param endpointURL
	 *            SQSのエンドポイント
	 * @param region
	 *            署名情報をリクエストするリージョン
	 * @param retryMax
	 *            失敗した時にリトライする回数
	 */
	public SQSClient(final String accessKey, final String secretKey, final String endpointURL, final String region,
			final int retryMax) {
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.endpointURL = endpointURL;
		this.region = region;
		this.retryMax = retryMax;
		this.client = this.build();
	}

	private AmazonSQS build() {
		log.debug("build SQS Client");
		final AmazonSQSClientBuilder builder = AmazonSQSClientBuilder.standard();

		final AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
		builder.withCredentials(new AWSStaticCredentialsProvider(credentials));

		final ClientConfiguration clientConfig = new ClientConfiguration();
		clientConfig.setConnectionTimeout((int) TimeUnit.SECONDS.toMillis(10));
		builder.withClientConfiguration(clientConfig);

		final EndpointConfiguration endpointConfig = new EndpointConfiguration(this.endpointURL, this.region);
		builder.withEndpointConfiguration(endpointConfig);

		return builder.build();
	}

	public SendMessageResult send(final String queueUrl, final String messageBody) throws AwsException {
		log.debug("SQS send start; to queue:{}, msg:{}", queueUrl, messageBody);
		final SendMessageRequest req = new SendMessageRequest(queueUrl, messageBody);
		for (int i = 0; i < this.retryMax; i++) {
			try {
				final SendMessageResult result = this.client.sendMessage(req);
				if (result != null) {
					log.debug("SQS send finish successfully; msg-id:{}", result.getMessageId());
					return result;
				}
			} catch (InvalidMessageContentsException | UnsupportedOperationException e) {
				throw new AwsException(e);
			}
		}
		throw new AwsException("Failed to send message");
	}

	public ReceiveMessageResult dequeue(final String queueUrl, final int num, final int timeoutSec)
			throws AwsException, InterruptedException {
		log.debug("SQS dequeue start; to qname:{}, num:{}, timeout :{}sec", queueUrl, num, timeoutSec);
		final ReceiveMessageRequest req = new ReceiveMessageRequest(queueUrl).withMaxNumberOfMessages(num)
				.withVisibilityTimeout(timeoutSec).withWaitTimeSeconds(20);
		for (int i = 0; i < this.retryMax; i++) {
			try {
				final ReceiveMessageResult result = this.client.receiveMessage(req);
				if (result != null) {
					log.debug("SQS dequeue finish successfully; msgsize:{}", result.getMessages().size());
					return result;
				}
			} catch (final OverLimitException e) {
				throw new AwsException(e);
			}
			TimeUnit.SECONDS.sleep(10);
		}
		throw new AwsException("Failed to dequeue");
	}

	public ReceiveMessageResult dequeue(final String queueUrl, final int timeoutSec)
			throws AwsException, InterruptedException {
		return this.dequeue(queueUrl, 1, timeoutSec);
	}

	public boolean delete(final String queueUrl, final String receiptHandle) throws AwsException {
		for (int i = 0; i < this.retryMax; i++) {
			try {
				final DeleteMessageResult result = this.client.deleteMessage(queueUrl, receiptHandle);
				if (result == null || result.getSdkHttpMetadata().getHttpStatusCode() >= 500) {
					continue;
				}
				log.debug("SQS delete finish successfully");
				return true;
			} catch (final ReceiptHandleIsInvalidException e) {
				throw new AwsException("Failed to dequeue", e);
			}
		}
		throw new AwsException("Failed to dequeue unexpectedly.");
	}
}
