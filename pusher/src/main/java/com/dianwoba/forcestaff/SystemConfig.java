package com.dianwoba.forcestaff;

import java.util.Properties;

public class SystemConfig {

	private String zkConnect;
	private int zkSessionTimeoutMs;
//	private int zkConnectionTimeoutMs;
	private int serverPort;

	public SystemConfig(Properties prop) {
		zkConnect = prop.getProperty("zookeeper.connect", "localhost:2181");
		zkSessionTimeoutMs = Integer.parseInt(prop.getProperty("zookeeper.session.timeout.ms", "5000"));
//		zkConnectionTimeoutMs = Integer.parseInt(prop.getProperty("zookeeper.connection.timeout.ms", "3600000"));
		serverPort = Integer.parseInt(prop.getProperty("server.port", "45678"));
	}

	public String getZkConnect() {
		return zkConnect;
	}

	public int getZkSessionTimeoutMs() {
		return zkSessionTimeoutMs;
	}

//	public int getZkConnectionTimeoutMs() {
//		return zkConnectionTimeoutMs;
//	}

	public int getServerPort() {
		return serverPort;
	}
}
