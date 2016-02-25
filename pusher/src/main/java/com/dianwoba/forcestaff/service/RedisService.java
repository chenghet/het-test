package com.dianwoba.forcestaff.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

	@Autowired
	RedisTemplate<String, String> redisTemplate;

	public String blpop(String key, long timeout, TimeUnit unit) {
		return redisTemplate.opsForList().leftPop(key, timeout, unit);
	}

	public String lpop(String key) {
		return redisTemplate.opsForList().leftPop(key);
	}

	public void rpush(String key, String value) {
		redisTemplate.opsForList().rightPush(key, value);
	}
}
