package com.dianwoba.pusher;

public enum WebsocketHandshakeHeader {
	VERSION("version"), APP_KEY("appKey"), TIMESTAMP("timestamp"), SIGN("sign"), FORMAT("format");

	private String headerKey;

	private WebsocketHandshakeHeader(String headerKey) {
		this.headerKey = headerKey;
	}

	public String getHeaderKey() {
		return headerKey;
	}
}