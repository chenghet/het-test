package com.dianwoba.forcestaff.sdk.link.ws;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;

/**
 * Created by het on 2016/4/13.
 */
public class HeartBeat2ServerTask implements Runnable {

    private NettyClient client;
    private Channel channel;

    public HeartBeat2ServerTask(NettyClient client, Channel channel) {
        this.client = client;
        this.channel = channel;
    }

    public void run() {
        // 如果ping次数超过5次没有回复
        if (client.getServerStateCheckTimes().get() > 5) {
            client.disconnect();
            try {
                client.connect();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        // Ping服务端
        PingWebSocketFrame ping = new PingWebSocketFrame();
        channel.writeAndFlush(ping);
        client.getServerStateCheckTimes().incrementAndGet();
    }
}