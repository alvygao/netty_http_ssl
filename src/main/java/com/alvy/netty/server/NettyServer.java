package com.alvy.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {

	private final int port;

	public NettyServer(int port) {
		this.port = port;
	}

	public void run() throws Exception {
		ServerBootstrap server = new ServerBootstrap();
		NioEventLoopGroup boss = new NioEventLoopGroup();
		NioEventLoopGroup worker = new NioEventLoopGroup();
		try {
			server.group(boss, worker)
					.channel(NioServerSocketChannel.class).localAddress(port)
					.childHandler(new DispatcherServletChannelInitializer());

			server.bind().sync().channel().closeFuture().sync();
		}
		finally {
			boss.shutdownGracefully();
			worker.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		int port;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		} else {
			port = 8080;
		}
		new NettyServer(port).run();
	}
}
