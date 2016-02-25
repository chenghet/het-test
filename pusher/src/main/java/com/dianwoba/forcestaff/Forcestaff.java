package com.dianwoba.forcestaff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dianwoba.forcestaff.core.ContextHolder;
import com.dianwoba.forcestaff.link.NettyServer;

/**
 * Push Server的启动器类
 * 
 * @author Administrator
 *
 */
public class Forcestaff {
	private static Logger logger = LoggerFactory.getLogger(Forcestaff.class);

	private final Ctx ctx;
	private NettyServer innerServer;

	public Forcestaff() {
		ctx = new Ctx();
		innerServer = new NettyServer(45678);
	}

	public void start() {
		innerServer.start();
	}

	public void shutdown() {
		innerServer.shutdown();
	}

	public Ctx getCtx() {
		return ctx;
	}

	public static void main(String[] args) {
		logger.info("Starting push server...");
		long t = System.currentTimeMillis();
		ApplicationContext appContext = new ClassPathXmlApplicationContext("classpath*:spring/app-context.xml");
		ContextHolder.setAppContext(appContext);
		long t0 = System.currentTimeMillis();
		logger.info("Application context initialized, cost {} ms", t0 - t);
		t = t0;

		Forcestaff forcestaff = new Forcestaff();
		ContextHolder.setCtx(forcestaff.getCtx());
	}
}