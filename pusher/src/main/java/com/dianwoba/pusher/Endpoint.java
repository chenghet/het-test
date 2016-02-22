package com.dianwoba.pusher;

import io.netty.channel.ChannelHandlerContext;

/**
 * 客户端连接的终端
 * 
 * @author Administrator
 */
public class Endpoint {

	private String appKey;
	private ChannelHandlerContext ctx;
	private long lastConmunicate;

	public Endpoint(String appKey) {
		this.appKey = appKey;
		lastConmunicate = System.currentTimeMillis();
	}

	public String getAppKey() {
		return appKey;
	}
}
