package com.dianwoba.forcestaff.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public final class ApplicationContextHolder {

	private static Logger logger = LoggerFactory.getLogger(ApplicationContextHolder.class);
	private static ApplicationContext appContext;

	private ApplicationContextHolder() {
	}

	public static ApplicationContext getAppContext() {
		while (appContext == null) {
			logger.warn("Application context is not setted.");
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				logger.warn("Thread sleep interrupted.");
			}
		}
		return appContext;
	}

	public static void setAppContext(ApplicationContext appContext) {
		ApplicationContextHolder.appContext = appContext;
	}
}