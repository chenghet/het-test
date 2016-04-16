package com.dianwoba.forcestaff.service.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis相关操作的Service
 * <p/>
 * Created by het on 2016/4/6.
 */
@Service("myRedisService")
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 出栈（阻塞）
     *
     * @param key
     * @param timeout
     * @param unit
     * @return
     */
    public String blpop(String key, long timeout, TimeUnit unit) {
        return redisTemplate.opsForList().leftPop(key, timeout, unit);
    }

    /**
     * 出栈
     *
     * @param key
     * @return
     */
    public String lpop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 入栈
     *
     * @param key
     * @param value
     */
    public void rpush(String key, String value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 查看队列长度
     *
     * @param key
     * @return
     */
    public long lsize(String key) {
        return redisTemplate.opsForList().size(key);
    }
}