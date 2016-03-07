package com.dianwoba.forcestaff.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndpointAliveCheckTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(EndpointAliveCheckTask.class);

	private Endpoint endpoint;

	public EndpointAliveCheckTask(Endpoint endpoint) {
		this.endpoint = endpoint;
	}

	public void run() {
		if (System.currentTimeMillis() - endpoint.getLastActive() > 30 * 60 * 1000) {
			logger.info("Endpoint is inavtive for 30 minutes. shutdowning...");
			endpoint.shutdown();
		}
	}

}
