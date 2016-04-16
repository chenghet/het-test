package com.dianwoba.forcestaff.sdk.link.ws;

import com.alibaba.fastjson.JSON;
import com.dianwoba.forcestaff.common.WebsocketHandshakeHeaderParam;
import com.dianwoba.forcestaff.sdk.Configuration;
import com.dianwoba.forcestaff.sdk.link.MessageHandler;
import com.dianwoba.forcestaff.tools.ResetableTimer;
import com.dianwoba.forcestaff.util.AuthenticationUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by het on 2016/4/8.
 */
public class NettyClient {

    private volatile AtomicBoolean connected = new AtomicBoolean(false);
    private Bootstrap bootstrap;

    private MessageHandler messageHandler;
    private Channel channel;

    private URI uri; // uri for web socket
    private WebSocketClientHandshaker handshaker;

    // refreshable
    private ResetableTimer serverStateChecker; //
    private volatile long lastServerActive; // 最后活动的时间戳
    private volatile AtomicInteger serverStateCheckTimes = new AtomicInteger(0);

    public NettyClient(Configuration config, MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        try {
            uri = new URI(config.getPushServerURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Configuration Error. For: " + JSON.toJSONString(config));
        }

        long timestamp = System.currentTimeMillis();
        // handshake header
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(WebsocketHandshakeHeaderParam.VERSION.getParamName(), "v1");
        headers.add(WebsocketHandshakeHeaderParam.APP_KEY.getParamName(), config.getAppKey());
        headers.add(WebsocketHandshakeHeaderParam.TIMESTAMP.getParamName(), timestamp);
        headers.add(WebsocketHandshakeHeaderParam.ACCEPT_FORMAT.getParamName(),
                WebsocketHandshakeHeaderParam.ACCEPT_FORMAT.getDefaultValue());
        // signature
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(WebsocketHandshakeHeaderParam.VERSION.getParamName(), "v1");
        map.put(WebsocketHandshakeHeaderParam.APP_KEY.getParamName(), config.getAppKey());
        map.put(WebsocketHandshakeHeaderParam.TIMESTAMP.getParamName(), timestamp);
        map.put(WebsocketHandshakeHeaderParam.ACCEPT_FORMAT.getParamName(),
                WebsocketHandshakeHeaderParam.ACCEPT_FORMAT.getDefaultValue());
        headers.add(WebsocketHandshakeHeaderParam.SIGN.getParamName(),
                AuthenticationUtil.sign(map, config.getAppSecret()));

        // handshake
        handshaker = WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, true, headers);
        // bootstrap
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new InnerHandler());
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    /**
     * 连接
     *
     * @throws Exception
     */
    public void connect() throws Exception {
        if (connected.get())
            return;

        // 失败重试3次
        for (int i = 0; i < 3; i++) {
            ChannelFuture future = bootstrap.connect(uri.getHost(), uri.getPort()).sync();
            if (future.isSuccess()) {
                channel = future.channel();
                connected.getAndSet(true);
                System.out.println("已经建立连接");
                // refresh
                serverStateChecker = new ResetableTimer(15000, new HeartBeat2ServerTask(NettyClient.this, channel));
                serverStateChecker.start();
                return;
            }
            System.out.println("连接服务失败，" + future.cause().getMessage());
            Thread.sleep(15000);
        }
        throw new Exception("连接服务失败");
    }

    /**
     * 终端连接
     */
    public void disconnect() {
        if (!connected.get()) {
            return;
        }
        try {
            serverStateChecker.stop();
            channel.close().sync();
        } catch (InterruptedException e) {
            // ignore
        } finally {
            connected.getAndSet(false);
        }
    }

    public void serverRefresh() {
        if (!connected.get()) {
            throw new RuntimeException("connection broken");
        }
        lastServerActive = System.currentTimeMillis();
        serverStateChecker.delay(15000);
        serverStateCheckTimes.set(0);
    }

    public AtomicInteger getServerStateCheckTimes() {
        return serverStateCheckTimes;
    }

    public ResetableTimer getServerStateChecker() {
        return serverStateChecker;
    }

    private class InnerHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new HttpClientCodec());
            pipeline.addLast(new ChunkedWriteHandler());
            pipeline.addLast(new HttpObjectAggregator(64 * 1024));
            pipeline.addLast(new DwdWebSocketClientProtocolHandler(handshaker));
            pipeline.addLast(new WebsocketMessageHandler(NettyClient.this, messageHandler));
        }
    }
}
