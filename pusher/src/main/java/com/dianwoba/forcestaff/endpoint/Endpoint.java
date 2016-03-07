package com.dianwoba.forcestaff.endpoint;

import io.netty.channel.Channel;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.dianwoba.forcestaff.core.ContextHolder;
import com.dianwoba.forcestaff.core.InnerException;
import com.dianwoba.forcestaff.message.MessageSource;
import com.dianwoba.forcestaff.message.MessageSourceFactory;
import com.dianwoba.forcestaff.tools.ResetableTimer;

/**
 * 客户端连接的终端
 * 
 * @author Het.C
 */
public class Endpoint {

	private String appKey;
	private String id;
	private Channel channel;

	private long lastActive; // 最后活动的时间戳
	private ResetableTimer aliveChecker; // 终端存活检查任务
	private ResetableTimer messageFetcher; // 终端存活检查任务
	private AtomicBoolean isAlive = new AtomicBoolean(false); // 判断终端是否存活
	private AtomicInteger continuousFailedTimes = new AtomicInteger(0);

	public Endpoint(String appKey, Channel channel) {
		this.appKey = appKey;
		this.channel = channel;
		this.id = getEndpointId(appKey, channel.remoteAddress().toString());
		lastActive = System.currentTimeMillis();
		aliveChecker = new ResetableTimer(60000, new EndpointAliveCheckTask(this));
		MessageSourceFactory factory = ContextHolder.getAppCtx().getBean(MessageSourceFactory.class);
		MessageSource messageSource = factory.getMessageSource(this);
		messageFetcher = new ResetableTimer(new MessageFetcherTask(this, messageSource));
		isAlive.set(true);
	}

	/**
	 * 启动
	 */
	public void start() {
		aliveChecker.start();
		messageFetcher.start();
	}

	/**
	 * 关闭
	 */
	public void shutdown() {
		if (isAlive.getAndSet(false)) {
			return;
		}
		try {
			channel.close();
			aliveChecker.stop();
			messageFetcher.stop();
		} catch (InterruptedException e) {
		}
	}

	/**
	 * 失败计数
	 */
	public void failedTick() {
		int i = continuousFailedTimes.incrementAndGet();
		if (i > 5) {
			shutdown();
		}
	}

	/**
	 * 保鲜
	 * 
	 * @throws InnerException
	 */
	public void refresh() throws InnerException {
		checkAlive();
		this.lastActive = System.currentTimeMillis();
	}

	public long getLastActive() {
		return lastActive;
	}

	public String getAppKey() {
		return appKey;
	}

	public AtomicBoolean isAlive() {
		return isAlive;
	}

	/**
	 * 获取Endpoint对应的Channel
	 * 
	 * @return
	 * @throws InnerException
	 */
	public Channel getChannel() throws InnerException {
		checkAlive();
		return this.channel;
	}

	public String getId() {
		return this.id;
	}

	/**
	 * 判定Endpoint是否存活状态
	 * 
	 * @throws InnerException
	 */
	private void checkAlive() throws InnerException {
		if (isAlive.get() == false) {
			throw new InnerException("Endpoint is not alive any more.");
		}
	}

	/**
	 * 获取Endpoint的ID
	 * 
	 * @param appKey
	 * @param addr
	 * @return
	 */
	public static final String getEndpointId(String appKey, String addr) {
		return appKey + "#" + addr;
	}
}