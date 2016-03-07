package com.dianwoba.forcestaff.link;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianwoba.forcestaff.core.ContextHolder;
import com.dianwoba.forcestaff.link.websocket.WebSocketServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class NettyServer {

	private static Logger logger = LoggerFactory.getLogger(NettyServer.class);

	private int port;
	private Channel serverChannel;
	private ServerBootstrap innerBootstrap;

	public NettyServer(int port) {
		this.port = port;
	}

	/**
	 * 启动
	 */
	public void start() {
		prepareBootstrap();
		bind();
	}

	/**
	 * 关闭
	 */
	public void shutdown() {
		innerBootstrap.group().shutdownGracefully();
		innerBootstrap.childGroup().shutdownGracefully();
		serverChannel.close();
	}

	/**
	 * 构造ServerBootstrap
	 * 
	 * @return
	 */
	private ServerBootstrap prepareBootstrap() {
		if (innerBootstrap != null)
			return innerBootstrap;

		innerBootstrap = new ServerBootstrap();
		EventLoopGroup boss = new NioEventLoopGroup();
		int processorNum = Runtime.getRuntime().availableProcessors();
		EventLoopGroup workers = new NioEventLoopGroup(processorNum * 2);
		innerBootstrap.channel(NioServerSocketChannel.class).group(boss, workers);
		innerBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel channel) throws Exception {
				ChannelPipeline pipeline = channel.pipeline();
				pipeline.addLast(new HttpServerCodec());
				pipeline.addLast(new ChunkedWriteHandler());
				pipeline.addLast(new HttpObjectAggregator(64 * 1024));
				pipeline.addLast(ContextHolder.getAppCtx().getBean(WebSocketServerHandler.class));
			}
		});
		return innerBootstrap;
	}

	/**
	 * 绑定端口
	 */
	public void bind() {
		ChannelFuture f = innerBootstrap.bind(port).syncUninterruptibly();
		f.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture f) throws Exception {
				if (f.isSuccess()) {
					serverChannel = f.channel();
				} else {
					logger.warn("Netty server binding port {} failed", port);
				}
			}
		});
	}
}
