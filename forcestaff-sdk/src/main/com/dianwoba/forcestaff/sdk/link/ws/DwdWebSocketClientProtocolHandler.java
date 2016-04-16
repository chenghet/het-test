package com.dianwoba.forcestaff.sdk.link.ws;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;

/**
 * Created by het on 2016/4/14.
 */
public class DwdWebSocketClientProtocolHandler extends WebSocketClientProtocolHandler {

    /**
     * Base constructor
     *
     * @param handshaker The {@link WebSocketClientHandshaker} which will be used to issue the handshake
     *                   once the connection was established to the remote peer.
     */
    public DwdWebSocketClientProtocolHandler(WebSocketClientHandshaker handshaker) {
        super(handshaker);
    }

    /**
     * Ping和Pong都由下游Handler处理
     *
     * @param ctx
     * @param frame
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
        if (frame instanceof CloseWebSocketFrame) {
            ctx.close();
            return;
        }
        out.add(frame.retain());
    }
}
