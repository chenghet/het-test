package com.dianwoba.forcestaff.sdk.link.ws;

import com.alibaba.fastjson.JSON;
import com.dianwoba.forcestaff.message.Message;
import com.dianwoba.forcestaff.sdk.link.LinkException;
import com.dianwoba.forcestaff.sdk.link.MessageHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by het on 2016/4/11.
 */
public class WebsocketMessageHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(WebsocketMessageHandler.class);

    private NettyClient client;
    private MessageHandler messageHandler;

    public WebsocketMessageHandler(NettyClient client, MessageHandler messageHandler) {
        this.client = client;
        this.messageHandler = messageHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof PingWebSocketFrame) {
            System.out.println("ping");
            ByteBuf content = ((PingWebSocketFrame) msg).content();
            ctx.writeAndFlush(new PongWebSocketFrame(content.retain()));
        } else if (msg instanceof TextWebSocketFrame) {
            String text = ((TextWebSocketFrame) msg).text();
            Message message = JSON.parseObject(text, Message.class);
            if (messageHandler != null) {
                messageHandler.handleMessage(message);
            } else {
                LOG.warn("Message duplicate, " + JSON.toJSONString(message));
            }
        } else if (msg instanceof PongWebSocketFrame) {
            // just ignore pong
            System.out.println("pong");
        }
        // fresh the server-state-holder whatever message received from server
        client.serverRefresh();
        // ignore other frame type
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        throw new LinkException("WebSocket occur Error", cause);
    }
}
