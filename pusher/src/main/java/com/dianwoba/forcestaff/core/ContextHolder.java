package com.dianwoba.forcestaff.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.dianwoba.forcestaff.Ctx;

public final class ContextHolder {

	private static Logger logger = LoggerFactory.getLogger(ContextHolder.class);
	private static ApplicationContext appContext;
	private static Ctx ctx;

	private ContextHolder() {
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
		ContextHolder.appContext = appContext;
	}
	
	public static void setCtx(Ctx ctx) {
		ContextHolder.ctx = ctx;
	}
	
	public static Ctx getCtx() {
		return ctx;
	}
}