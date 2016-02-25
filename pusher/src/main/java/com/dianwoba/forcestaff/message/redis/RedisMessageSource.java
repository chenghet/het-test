package com.dianwoba.forcestaff.message.redis;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianwoba.forcestaff.endpoint.Endpoint;
import com.dianwoba.forcestaff.message.Message;
import com.dianwoba.forcestaff.message.MessageSource;
import com.dianwoba.forcestaff.message.TopicTypes;
import com.dianwoba.forcestaff.service.RedisService;

public class RedisMessageSource implements MessageSource {
	
	@Autowired
	private RedisService redisService;
	private Endpoint endpoint;
	private String orderCacheKey;
	private String failedCacheKey;
	
	public Message fetchMessage() {
		String content = redisService.lpop(failedCacheKey);
		if (content == null) {
			content = redisService.lpop(orderCacheKey);
		}
		Message msg = new Message();
		msg.setAppKey(endpoint.getAppKey());
		msg.setTopic(TopicTypes.orderInfo.name());
		msg.setContent(content);
		return msg;
	}

	public Message[] batchFetchMessages() {
		throw new UnsupportedOperationException();
	}

	public void failedMessageSink(Message msg) {
		redisService.rpush(failedCacheKey, msg.getContent());
	}
}
