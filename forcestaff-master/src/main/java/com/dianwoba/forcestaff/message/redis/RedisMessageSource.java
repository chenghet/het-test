package com.dianwoba.forcestaff.message.redis;

import com.dianwoba.forcestaff.endpoint.Endpoint;
import com.dianwoba.forcestaff.message.Message;
import com.dianwoba.forcestaff.message.MessageSource;
import com.dianwoba.forcestaff.message.MessageTopics;
import com.dianwoba.forcestaff.service.redis.RedisService;

public class RedisMessageSource implements MessageSource {

    private RedisService redisService;
    private Endpoint endpoint;
    private String orderCacheKey;
    private String failedCacheKey;

    public RedisMessageSource(Endpoint endpoint, RedisService redisService) {
        this.endpoint = endpoint;
        this.redisService = redisService;
        orderCacheKey = "PLATFORM_ORDER_STATUS_LIST_" + endpoint.getAppKey();
        failedCacheKey = "PLATFORM_ORDER_STATUS_FAILED_LIST_" + endpoint.getAppKey();
    }

    public Message fetchMessage() {
//		String content = redisService.lpop(failedCacheKey);
//		if (content == null) {
//			content = redisService.lpop(orderCacheKey);
//		}
//        if (content == null) {
//            return null;
//        }
        String content = "t" + System.currentTimeMillis();
        Message msg = new Message();
        msg.setAppKey(endpoint.getAppKey());
        msg.setTopic(MessageTopics.orderStatus.name());
        msg.setContent(content);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // do nothing
        }
        return msg;
//		return null;
    }

    public Message[] batchFetchMessages() {
        throw new UnsupportedOperationException();
    }

    public void failedMessageSink(Message msg) {
        redisService.rpush(failedCacheKey, msg.getContent());
    }
}
