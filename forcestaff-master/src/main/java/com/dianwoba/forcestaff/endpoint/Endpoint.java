package com.dianwoba.forcestaff.endpoint;

import com.dianwoba.forcestaff.core.ContextHolder;
import com.dianwoba.forcestaff.core.InnerException;
import com.dianwoba.forcestaff.message.MessageSource;
import com.dianwoba.forcestaff.message.MessageSourceFactory;
import com.dianwoba.forcestaff.tools.ResetableTimer;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 客户端连接的终端
 *
 * @author Het.C
 */
public class Endpoint {

    private static final Logger logger = LoggerFactory.getLogger(Endpoint.class);

    private String appKey;
    private String id;
    private Channel channel;

    private long lastActive; // 最后活动的时间戳
    private ResetableTimer aliveChecker; // 终端存活检查任务
    private ResetableTimer messageFetcher; // 终端存活检查任务
    private AtomicBoolean isAlive = new AtomicBoolean(false); // 判断终端是否存活
    private AtomicInteger continuousFailedTimes = new AtomicInteger(0);

    /**
     * 客户端保持在服务端的终端信息
     *
     * @param appKey
     * @param channel
     */
    public Endpoint(String appKey, Channel channel) {
        this.appKey = appKey;
        this.channel = channel;
        this.id = getEndpointId(appKey, channel.remoteAddress().toString());
        lastActive = System.currentTimeMillis();
        aliveChecker = new ResetableTimer(15000, new EndpointAliveCheckTask(this));
        MessageSourceFactory factory = ContextHolder.getAppCtx().getBean(MessageSourceFactory.class);
        MessageSource messageSource = factory.getMessageSource(this);
        messageFetcher = new ResetableTimer(new MessageFetcherTask(this, messageSource));
        isAlive.set(true);
    }

    /**
     * 获取Endpoint的ID
     *
     * @param appKey
     * @param addr
     * @return
     */
    public static final String getEndpointId(String appKey, String addr) {
        return appKey + "#" + addr;
    }

    /**
     * 启动
     */
    public void start() {
        aliveChecker.start();
        messageFetcher.start();
        logger.info("Endpoint[{}] started.", getId());
    }

    /**
     * 关闭
     */
    public void shutdown() {
        if (!isAlive.get()) {
            return;
        }
        try {
            messageFetcher.stop();
            aliveChecker.stop();
            Thread.sleep(50);
            channel.close();
        } catch (InterruptedException e) {
        } finally {
            isAlive.set(false);
            ContextHolder.getCtx().unregisterEndpoint(this);
            logger.info("Endpoint[{}] shutdown.", getId());
        }
    }

    /**
     * 失败计数
     */
    public void failedTick() {
        if (isAlive.get()) {
            int i = continuousFailedTimes.incrementAndGet();
            messageFetcher.delay(60000); // 延后5分钟重试
            if (i > 5) {
                shutdown();
            }
        }
    }

    /**
     * 保鲜
     *
     * @throws InnerException
     */
    public void refresh() throws InnerException {
        checkAlive();
        continuousFailedTimes.set(0);
        this.lastActive = System.currentTimeMillis();
        this.aliveChecker.delay(6000);
    }

    public long getLastActive() {
        return lastActive;
    }

    public String getAppKey() {
        return appKey;
    }

    /**
     * 获取Endpoint对应的Channel
     *
     * @return
     * @throws InnerException
     */
    public Channel getChannel() throws InnerException {
        checkAlive();
        return this.channel;
    }

    public String getId() {
        return this.id;
    }

    /**
     * 判定Endpoint是否存活状态
     *
     * @throws InnerException
     */
    private void checkAlive() throws InnerException {
        if (!isAlive.get()) {
            throw new InnerException(String.format("Endpoint[%s] is not alive any more.", id));
        }
    }

    @Override
    public String toString() {
        return getId();
    }
}