package com.dianwoba.forcestaff.message.redis;

import com.dianwoba.forcestaff.endpoint.Endpoint;
import com.dianwoba.forcestaff.message.MessageSource;
import com.dianwoba.forcestaff.message.MessageSourceFactory;

public class RedisMessageSourceFactory extends MessageSourceFactory {

	@Override
	public MessageSource getMessageSource(Endpoint endpoint) {
		MessageSource source = new RedisMessageSource(endpoint);
		return source;
	}
}
