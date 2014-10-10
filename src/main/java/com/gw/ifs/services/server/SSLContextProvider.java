package com.gw.ifs.services.server;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import com.gw.ifs.services.common.SSLConfigBuilder;
import com.gw.ifs.services.common.SSLConfiguration;

public class SSLContextProvider {

	private static final String ALGORITHM = "SunX509";
	private static final String PROTOCOL = "TLS";
	private static final SSLContext SERVER_CONTEXT;

	static {
		SSLContext serverContext;
		try {

            SSLConfiguration sslConfiguration = SSLConfigBuilder.getSslConfiguration();
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(sslConfiguration.getKeyStoreFile()),
					sslConfiguration.getKeyStorePassword().toCharArray());
			KeyStore tks = KeyStore.getInstance("JKS");
			tks.load(new FileInputStream(sslConfiguration.getTrustStoreFile()),
					sslConfiguration.getTrustStorePassword().toCharArray());

			// Set up key manager factory to use our key store
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(ALGORITHM);
			TrustManagerFactory tmf = TrustManagerFactory
					.getInstance("SunX509");
			kmf.init(ks, sslConfiguration.getKeyStorePassword().toCharArray());
			tmf.init(tks);

			// Initialize the SSLContext to work with our key managers.
			serverContext = SSLContext.getInstance(PROTOCOL);
			serverContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(),
					null);
		} catch (Exception e) {
			throw new Error("Failed to initialize the server-side SSLContext",
					e);
		}

		SERVER_CONTEXT = serverContext;
	}

	public static SSLContext getServerContext() {
		return SERVER_CONTEXT;
	}
}
