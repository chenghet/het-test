package com.dianwoba.forcestaff.message.redis;

import com.dianwoba.forcestaff.endpoint.Endpoint;
import com.dianwoba.forcestaff.message.MessageSource;
import com.dianwoba.forcestaff.message.MessageSourceFactory;
import com.dianwoba.forcestaff.service.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;

public class RedisMessageSourceFactory extends MessageSourceFactory {

    @Autowired
    private RedisService redisService;

    @Override
    public MessageSource getMessageSource(Endpoint endpoint) {
        MessageSource source = new RedisMessageSource(endpoint, redisService);
        return source;
    }
}
