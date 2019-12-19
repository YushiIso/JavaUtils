package com.github.yi.util.client.aws;

public class AwsException extends Exception {
	private static final long serialVersionUID = 1L;

	public AwsException() {
	}

	public AwsException(String message) {
		super(message);
	}

	public AwsException(Throwable cause) {
		super(cause);
	}

	public AwsException(String message, Throwable cause) {
		super(message, cause);
	}

	public AwsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
