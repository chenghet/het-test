package com.dianwoba.pusher;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Bootstrap {
	public static void main(String[] args) {
		ApplicationContext appContext = new ClassPathXmlApplicationContext("classpath*:spring/app-context.xml");
		ApplicationContextHolder.setAppContext(appContext);
		
		
	}
}
