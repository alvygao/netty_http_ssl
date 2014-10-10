package com.alvy.netty.common;

public class SSLConfiguration {

	private String trustStoreFile;
	private String trustStorePassword;
	private String keyStoreFile;
	private String keyStorePassword;
	private boolean enableSsl;

	public String getTrustStoreFile() {
		return trustStoreFile;
	}

	public void setTrustStoreFile(String trustStoreFile) {
		this.trustStoreFile = trustStoreFile;
	}

	public String getTrustStorePassword() {
		return trustStorePassword;
	}

	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStorePassword = trustStorePassword;
	}

	public String getKeyStoreFile() {
		return keyStoreFile;
	}

	public void setKeyStoreFile(String keyStoreFile) {
		this.keyStoreFile = keyStoreFile;
	}

	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	public boolean getEnableSsl() {
		return enableSsl;
	}

	public void setEnableSsl(boolean enableSsl) {
		this.enableSsl = enableSsl;
	}

}
