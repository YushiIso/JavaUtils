package com.github.yi.crawler;

import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;

public class HttpClientTimeUtil {
	@Getter
	@Setter
	private TimeUnit timeoutUnit = TimeUnit.SECONDS;
	@Getter
	@Setter
	private long timeout = 30;

	@Getter
	@Setter
	private TimeUnit sleepTimeUnit = TimeUnit.SECONDS;
	@Getter
	@Setter
	private long sleepTime = TimeUnit.SECONDS.toMillis(5);
	@Getter
	@Setter
	private boolean sleep = true;

	protected int getTimeoutToMillis() {
		return (int) this.timeoutUnit.toMillis(this.timeout);
	}

	protected void sleep() throws InterruptedException {
		if (this.sleep) {
			this.sleepTimeUnit.sleep(this.sleepTime);
		}
	}
}
