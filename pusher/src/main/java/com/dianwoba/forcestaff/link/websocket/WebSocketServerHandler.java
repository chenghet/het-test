package com.dianwoba.forcestaff.link.websocket;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.ssl.SslHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dianwoba.forcestaff.core.ContextHolder;
import com.dianwoba.forcestaff.core.InnerException;
import com.dianwoba.forcestaff.endpoint.Endpoint;
import com.dianwoba.forcestaff.link.auth.AuthenticationException;
import com.dianwoba.forcestaff.link.auth.AuthenticationInfo;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WebSocketServerHandler extends ChannelInboundHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(WebSocketServerHandler.class);

	private String websocketPath = "/";
	@Autowired
	private WebsocketHandshakeAuthenticationManager authManager;

	private WebSocketServerHandshaker handshaker;

	private AuthenticationInfo auth;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof FullHttpRequest) {
			handleHttpRequest(ctx, (FullHttpRequest) msg);
		} else if (msg instanceof WebSocketFrame) {
			handleWebsocketFrame(ctx, (WebSocketFrame) msg);
		}
	}

	/**
	 * 处理Http请求，即Websocket握手EEE
	 * 
	 * @param ctx
	 * @param req
	 */
	public void handleHttpRequest(final ChannelHandlerContext ctx, final FullHttpRequest req) {
		try {
			// Bad request
			if (!req.getDecoderResult().isSuccess()) {
				logger.warn("【handshake-failed】Bad request from {}", ctx.channel().remoteAddress());
				sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
				return;
			}

			// Websocket握手必须采用websocket形式
			if (req.getMethod() != GET) {
				sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
				logger.warn("【handshake-failed】Unsupportable http methed GET from", ctx.channel().remoteAddress());
				return;
			}

			// 握手授权校验
			try {
				auth = authManager.authenticate(req);
			} catch (AuthenticationException e) {
				ByteBuf buf = Unpooled.buffer().writeBytes(e.toString().getBytes());
				sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN, buf));
				logger.warn("【handshake-failed】authenticate failed from {} for {}", ctx.channel().remoteAddress(), e);
				return;
			}

			// 进行Websocket握手
			WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(ctx.pipeline(), req,
					this.websocketPath), null, true);
			final WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
			if (handshaker == null) {
				WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
			} else {
				ChannelFuture handshakeFuture = handshaker.handshake(ctx.channel(), req);
				handshakeFuture.addListener(new ChannelFutureListener() {
					public void operationComplete(ChannelFuture future) throws Exception {
						if (future.isSuccess()) {
							// Websocket握手成功
							WebSocketServerHandler.this.handshaker = handshaker;
							ctx.pipeline().replace(WebSocketServerHandler.this, "WS403Responder", forbiddenHttpRequestResponder());

							// 注册Endpoint
							Endpoint ep = new Endpoint(auth.getAppKey(), future.channel());
							ContextHolder.getCtx().registerEndpoint(ep);
						} else {
							// Websocket握手失败
							ByteBuf buf = Unpooled.buffer().writeBytes("Websocket握手失败，连接失败！".getBytes());
							sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN, buf));
						}
					}

				});
			}
		} finally {
			req.release();
		}
	}

	/**
	 * 发送HttpResponse
	 * 
	 * @param ctx
	 * @param req
	 * @param resp
	 */
	private static void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse resp) {
		ChannelFuture f = ctx.channel().writeAndFlush(resp);
		if (!HttpHeaders.isKeepAlive(req) || resp.getStatus().code() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	/**
	 * Build websocket location
	 * 
	 * @param cp
	 * @param req
	 * @param path
	 * @return
	 */
	private static String getWebSocketLocation(ChannelPipeline cp, HttpRequest req, String path) {
		String protocol = "ws";
		if (cp.get(SslHandler.class) != null) {
			protocol = "wss";
		}
		return protocol + "://" + HttpHeaders.getHost(req) + path;
	}

	/**
	 * Forbid HTTP Request after handshaked
	 * 
	 * @return
	 */
	static ChannelHandler forbiddenHttpRequestResponder() {
		return new ChannelInboundHandlerAdapter() {
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				if (msg instanceof FullHttpRequest) {
					((FullHttpRequest) msg).release();
					FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN);
					ctx.channel().writeAndFlush(response);
				} else {
					ctx.fireChannelRead(msg);
				}
			}
		};
	}

	/**
	 * Handle websocket frame
	 * 
	 * @param ctx
	 * @param frame
	 */
	private void handleWebsocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
		// 判断是否关闭链路的指令
		if (frame instanceof CloseWebSocketFrame) {
			ChannelFuture f = handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
			// 不论close handshake是否成功，注销Endpoint.
			f.addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture f0) throws Exception {
					Endpoint endpoint = ContextHolder.getCtx().findEndpoint(auth.getAppKey(), remoteAddr(f0.channel()));
					ContextHolder.getCtx().unregisterEndpoint(endpoint);
					endpoint.shutdown();
				}
			});
		}
		// 判断是否ping消息
		if (frame instanceof PingWebSocketFrame) {
			ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
			// Endpoint保鲜
			Endpoint endpoint = ContextHolder.getCtx().findEndpoint(auth.getAppKey(), remoteAddr(ctx.channel()));
			endpoint.refresh();
			return;
		}
		throw new UnsupportedOperationException("消息推送服务不支持任何消息请求！");
	}

	/**
	 * 获取Endpoint的id号
	 * 
	 * @param appKey
	 * @param channel
	 * @return
	 */
	public static String remoteAddr(Channel channel) {
		if (channel == null || !channel.isActive()) {
			throw new InnerException("channel is null or not active");
		}
		return channel.remoteAddress().toString();
	}

	public String getWebsocketPath() {
		return websocketPath;
	}

	public void setWebsocketPath(String websocketPath) {
		this.websocketPath = websocketPath;
	}
}
