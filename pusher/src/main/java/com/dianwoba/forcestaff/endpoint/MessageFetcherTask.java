package com.dianwoba.forcestaff.endpoint;

import java.util.concurrent.atomic.AtomicBoolean;

import com.alibaba.fastjson.JSON;
import com.dianwoba.forcestaff.message.Message;
import com.dianwoba.forcestaff.message.MessageSource;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class MessageFetcherTask implements Runnable {

	private Endpoint endpoint;
	private MessageSource messageSource;
	private AtomicBoolean stop = new AtomicBoolean(false);

	public MessageFetcherTask(Endpoint endpoint, MessageSource messageSource) {
		this.endpoint = endpoint;
		this.messageSource = messageSource;
	}

	public void run() {
		while (!stop.get()) {
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

	/**
	 * 关闭
	 */
	public void shutdown() {
		stop.set(true);
	}
}
