package com.dianwoba.pusher;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * 客户端连接的终端
 * 
 * @author Administrator
 */
public class Endpoint {
	private String appKey;
	private ChannelHandlerContext ctx;
	private long lastActive; // 最后一次通信的时间戳

	public Endpoint(String appKey, ChannelHandlerContext ctx) {
		this.appKey = appKey;
		this.ctx = ctx;
		lastActive = System.currentTimeMillis();
	}

	public String getAppKey() {
		return appKey;
	}

	public long getLastActive() {
		return lastActive;
	}

	public Channel getChannel() {
		return ctx.channel();
	}
}