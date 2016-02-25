package com.dianwoba.forcestaff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dianwoba.forcestaff.core.ApplicationContextHolder;

/**
 * Push Server的启动器类
 * 
 * @author Administrator
 *
 */
public class Bootstrap {
	private static Logger logger = LoggerFactory.getLogger(Bootstrap.class);

	public static void main(String[] args) {
		logger.info("Starting push server...");
		long t = System.currentTimeMillis();
		ApplicationContext appContext = new ClassPathXmlApplicationContext("classpath*:spring/app-context.xml");
		ApplicationContextHolder.setAppContext(appContext);
		long t0 = System.currentTimeMillis();
		logger.info("Application context initialized, cost {} ms",  t0 - t);
		t = t0;
		
		
	}
}
