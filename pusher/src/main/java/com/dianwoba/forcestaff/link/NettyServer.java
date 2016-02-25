package com.dianwoba.forcestaff.link;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

import com.dianwoba.forcestaff.link.websocket.WebSocketServerHandler;

public class NettyServer {

	private int port;
	private ServerBootstrap innerBootstrap;

	public NettyServer(int port) {
		this.port = port;
	}

	public void start() {
		prepareBootstrap().bind();
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
				pipeline.addLast(new WebSocketServerHandler("/ws"));
			}
		});
		return innerBootstrap;
	}

	/**
	 * 绑定端口
	 */
	public void bind() {
		ChannelFuture f = innerBootstrap.bind(port).syncUninterruptibly();
		
	}
}
