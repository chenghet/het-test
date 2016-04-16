package com.dianwoba.forcestaff.endpoint;

import com.alibaba.fastjson.JSON;
import com.dianwoba.forcestaff.message.Message;
import com.dianwoba.forcestaff.message.MessageSource;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageFetcherTask implements Runnable {

    public static final Logger LOG = LoggerFactory.getLogger(Endpoint.class);

    private Endpoint endpoint;
    private MessageSource messageSource;

    public MessageFetcherTask(Endpoint endpoint, MessageSource messageSource) {
        this.endpoint = endpoint;
        this.messageSource = messageSource;
    }

    public void run() {
        final Message msg = messageSource.fetchMessage();
        if (msg != null) {
            TextWebSocketFrame frame = new TextWebSocketFrame(JSON.toJSON(msg).toString());
            ChannelFuture f = endpoint.getChannel().writeAndFlush(frame);
            f.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        endpoint.refresh();
                        LOG.info("Message to Endpoint[{}] success, msg: [{}]", this, msg);
                    } else {
                        // FIXME
                        // messageSource.failedMessageSink(msg);
                        LOG.info("Message to Endpoint[{}] failed, msg: [{}]", this, msg);
                        endpoint.failedTick();
                        return;
                    }
                }
            });
        } else {
            // 如果没有消息，则休眠5000ms后重试
            try {
                LOG.debug("No message yet. sleep 5000 ms.");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }
}
