package com.gw.ifs.services.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.servlet.ServletException;

import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.gw.ifs.services.common.SSLConfigBuilder;
import com.gw.ifs.services.common.SSLConfiguration;

public class DispatcherServletChannelInitializer extends
		ChannelInitializer<SocketChannel> {

	private final DispatcherServlet dispatcherServlet;

	public DispatcherServletChannelInitializer() throws ServletException {

		MockServletContext servletContext = new MockServletContext();
		MockServletConfig servletConfig = new MockServletConfig(servletContext);

		XmlWebApplicationContext wac = new XmlWebApplicationContext();
		wac.setConfigLocation("classpath:spring/applicationContext.xml");
		wac.setServletContext(servletContext);
		wac.setServletConfig(servletConfig);
		wac.refresh();

		this.dispatcherServlet = new DispatcherServlet(wac);
		this.dispatcherServlet.init(servletConfig);
	}

	@Override
	public void initChannel(SocketChannel channel) throws Exception {
		ChannelPipeline pipeline = channel.pipeline();
		SSLConfiguration sslConfiguration = SSLConfigBuilder
				.getSslConfiguration();
		if (sslConfiguration != null && sslConfiguration.getEnableSsl()) {
			
			SSLContext sslContext = SSLContextProvider.getServerContext();
			SSLEngine sslEngine = sslContext.createSSLEngine();
			sslEngine.setUseClientMode(false);
			sslEngine.setNeedClientAuth(true);
			SslHandler sslHandler = new SslHandler(sslEngine);
			pipeline.addLast("ssl", sslHandler);
		}

		pipeline.addLast("decoder", new HttpRequestDecoder());
		pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
		pipeline.addLast("encoder", new HttpResponseEncoder());
		pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
		pipeline.addLast("handler", new ServletNettyHandler(
				this.dispatcherServlet));
	}
}
