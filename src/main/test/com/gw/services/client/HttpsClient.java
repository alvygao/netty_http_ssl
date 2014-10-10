/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.gw.services.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.ClientCookieEncoder;
import io.netty.handler.codec.http.DefaultCookie;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

import java.io.FileInputStream;
import java.net.URI;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * A simple HTTP client that prints out the content of the HTTP response to
 * {@link System#out} to test {@link HttpSnoopServer}.
 */
public final class HttpsClient {

	static final String URL = System.getProperty("url",
			"https://10.15.44.62:8080/");

	private static String CLIENT_KEY_STORE = "C:\\Certificates\\netty\\client.p12";
	private static String CLIENT_TRUST_KEY_STORE = "C:\\Certificates\\netty\\client.truststore";
	private static String CLIENT_KEY_STORE_PASSWORD = "123456";
	private static String CLIENT_TRUST_KEY_STORE_PASSWORD = "123456";

	private static final SSLContext CLIENT_CONTEXT;
	private static final String ALGORITHM = "SunX509";
	private static final String PROTOCOL = "TLS";

	static {
		try {
			KeyStore ks2 = KeyStore.getInstance("PKCS12");
			ks2.load(new FileInputStream(CLIENT_KEY_STORE),
					CLIENT_KEY_STORE_PASSWORD.toCharArray());

			KeyStore tks2 = KeyStore.getInstance("JKS");
			tks2.load(new FileInputStream(CLIENT_TRUST_KEY_STORE),
					CLIENT_TRUST_KEY_STORE_PASSWORD.toCharArray());
			// Set up key manager factory to use our key store
			KeyManagerFactory kmf2 = KeyManagerFactory.getInstance(ALGORITHM);
			TrustManagerFactory tmf2 = TrustManagerFactory
					.getInstance("SunX509");
			kmf2.init(ks2, CLIENT_KEY_STORE_PASSWORD.toCharArray());
			tmf2.init(tks2);
			CLIENT_CONTEXT = SSLContext.getInstance(PROTOCOL);
			CLIENT_CONTEXT.init(kmf2.getKeyManagers(), tmf2.getTrustManagers(),
					null);
		} catch (Exception e) {
			throw new Error("Failed to initialize the client-side SSLContext",
					e);
		}
	}

	public static void main(String[] args) throws Exception {
		URI uri = new URI(URL);
		String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
		String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
		int port = uri.getPort();
		if (port == -1) {
			if ("http".equalsIgnoreCase(scheme)) {
				port = 80;
			} else if ("https".equalsIgnoreCase(scheme)) {
				port = 443;
			}
		}

		if (!"http".equalsIgnoreCase(scheme)
				&& !"https".equalsIgnoreCase(scheme)) {
			System.err.println("Only HTTP(S) is supported.");
			return;
		}

		// Configure SSL context if necessary.
		final boolean ssl = "https".equalsIgnoreCase(scheme);
		final SSLContext sslCtx;
		if (ssl) {
			sslCtx = CLIENT_CONTEXT;
		} else {
			sslCtx = null;
		}

		// Configure the client.
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
					.handler(new HttpsClientInitializer(sslCtx));

			// Make the connection attempt.
			Channel ch = b.connect(host, port).sync().channel();

			String requestBody = "{\"productId\": 11,\"userName\": \"caiwei\",\"amount\": 1000}";
			ByteBuf content = Unpooled.copiedBuffer(requestBody.getBytes(CharsetUtil.UTF_8));
			// Prepare the HTTP request.
			HttpRequest request = new DefaultFullHttpRequest(
					HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getRawPath(), content);
			request.headers().set(HttpHeaders.Names.HOST, host);
			request.headers().set(HttpHeaders.Names.CONNECTION,
					HttpHeaders.Values.CLOSE);
			request.headers().set(HttpHeaders.Names.ACCEPT_ENCODING,
					HttpHeaders.Values.GZIP);

			request.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json");
			request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, content.readableBytes());
			// Set some example cookies.
			request.headers()
					.set(HttpHeaders.Names.COOKIE,
							ClientCookieEncoder.encode(new DefaultCookie(
									"my-cookie", "foo"), new DefaultCookie(
									"another-cookie", "bar")));
			// Send the HTTP request.
			ch.writeAndFlush(request);

			// Wait for the server to close the connection.
			ch.closeFuture().sync();
		} finally {
			// Shut down executor threads to exit.
			group.shutdownGracefully();
		}
	}
}
