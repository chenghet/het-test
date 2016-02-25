package com.dianwoba.forcestaff.endpoint;

import io.netty.channel.Channel;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianwoba.forcestaff.ResetableTimer;
import com.dianwoba.forcestaff.core.InnerException;

/**
 * 客户端连接的终端
 * 
 * @author Administrator
 */
public class Endpoint {
	private static Logger logger = LoggerFactory.getLogger(Endpoint.class);

	private String appKey;
	private String id;
	private Channel channel;
	
	private long lastActive; // 最后活动的时间戳
	private ResetableTimer aliveChecker; // 终端存活检查任务
	private AtomicBoolean isAlive = new AtomicBoolean(false); // 判断终端是否存活
	private AtomicInteger continuousFailedTimes = new AtomicInteger(0);
	
	public Endpoint(String appKey, Channel channel) {
		this.appKey = appKey;
		this.channel = channel;
		this.id = appKey + "#" + channel.remoteAddress();
		lastActive = System.currentTimeMillis();
		aliveChecker = new ResetableTimer(60000, new Runnable() {
			public void run() {
				if (System.currentTimeMillis() - lastActive > 30 * 60 * 1000) {
					logger.info("Endpoint is inavtive for 30 minutes. shutdowning...");
					shutdown();
				}
			}
		}).start();
		isAlive.set(true);
	}

	public void shutdown() {
		if (isAlive.getAndSet(false)) {
			return;
		}
		try {
			channel.close();
			aliveChecker.stop();
		} catch (InterruptedException e) {
		}
	}
	
	public void failedTick() {
		int i = continuousFailedTimes.incrementAndGet();
		if (i > 5) {
			shutdown();
		}
	}

	public void refresh() throws InnerException {
		checkAlive();
		this.lastActive = System.currentTimeMillis();
	}

	public String getAppKey() {
		return appKey;
	}

	public AtomicBoolean isAlive() {
		return isAlive;
	}

	public Channel getChannel() throws InnerException {
		checkAlive();
		return this.channel;
	}
	
	public String getId() {
		return this.id;
	}

	private void checkAlive() throws InnerException {
		if (isAlive.get() == false) {
			throw new InnerException("Endpoint is not alive any more.");
		}
	}
}