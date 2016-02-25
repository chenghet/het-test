package com.dianwoba.forcestaff.message;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import com.alibaba.fastjson.JSON;
import com.dianwoba.forcestaff.endpoint.Endpoint;

public class MessageFetcher extends Thread {
	
	private Endpoint endpoint;
	private MessageSource messageSource;
	private boolean stop = false;
	
	public MessageFetcher(Endpoint endpoint, MessageSource messageSource) {
		this.endpoint = endpoint;
		this.messageSource = messageSource;
		// 守护线程
		this.setDaemon(true);
	}
	
	@Override
	public void run() {
		while(!stop) {
			final Message msg = messageSource.fetchMessage();
			if (msg != null) {
				ChannelFuture f = endpoint.getChannel().writeAndFlush(JSON.toJSON(msg));
				f.addListener(new ChannelFutureListener() {
					public void operationComplete(ChannelFuture future) throws Exception {
						if (!future.isSuccess()) {
							messageSource.failedMessageSink(msg);
							return;
						}
						endpoint.refresh();
					}
				});
			} else {
				// 如果没有消息，则休眠5000ms后重试
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
			}
		}
	}
}
