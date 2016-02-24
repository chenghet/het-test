package com.dianwoba.pusher;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.util.concurrent.atomic.AtomicBoolean;

public class NettyServer {
	
	private AtomicBoolean prepared = new AtomicBoolean(false);
	private int port;
	private ServerBootstrap innerBootstrap;

	public NettyServer() {
		innerBootstrap = new ServerBootstrap();
		prepare();
	}

	public void prepare() {
		if (prepared.get() == true)
			return;
		
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
				pipeline.addLast(new DWBWebSocketServerHandler("/ws"));
			}
		});
		prepared.getAndSet(true);
	}
	
	public void bind(){
		innerBootstrap.bind(port);
	}
}
