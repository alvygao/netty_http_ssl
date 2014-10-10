package com.alvy.netty.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSLConfigBuilder {

	private static SSLConfiguration sslConfiguration;
	private static final String CONFIG_FILE = "sslconfig.properties";
	private static Logger logger = LoggerFactory
			.getLogger(SSLConfigBuilder.class);

	public static SSLConfiguration getSslConfiguration() {
		if (sslConfiguration == null) {
			sslConfiguration = buildSslConfiguration();
		}
		return sslConfiguration;
	}

	private static SSLConfiguration buildSslConfiguration() {

		Properties properties = new Properties();
		InputStream inputStream = SSLConfigBuilder.class.getClassLoader()
				.getResourceAsStream(CONFIG_FILE);
		SSLConfiguration sslConfiguration = new SSLConfiguration();
		try {
			properties.load(inputStream);
			String trustStoreFile = properties
					.getProperty("netty.ssl.truststore.file");
			String keyStoreFile = properties
					.getProperty("netty.ssl.keystore.file");
			String trustStorePwd = properties
					.getProperty("netty.ssl.truststore.password");
			String keyStorePwd = properties
					.getProperty("netty.ssl.keystore.password");
			boolean enableSsl = Boolean.valueOf(properties
					.getProperty("netty.ssl.enable"));
			sslConfiguration.setKeyStoreFile(keyStoreFile);
			sslConfiguration.setKeyStorePassword(keyStorePwd);
			sslConfiguration.setTrustStoreFile(trustStoreFile);
			sslConfiguration.setTrustStorePassword(trustStorePwd);
			sslConfiguration.setEnableSsl(enableSsl);

		} catch (IOException e) {
			logger.info("error occurs when read ssl configuration");
		}
		return sslConfiguration;
	};
}
