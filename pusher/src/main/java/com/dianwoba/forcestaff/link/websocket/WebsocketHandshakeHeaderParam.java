package com.dianwoba.forcestaff.link.websocket;

public enum WebsocketHandshakeHeaderParam {
	VERSION("version"), APP_KEY("appKey"), TIMESTAMP("timestamp"), SIGN("sign"), ACCEPT_FORMAT("accept-format");

	private String paramName;

	private WebsocketHandshakeHeaderParam(String paramName) {
		this.paramName = paramName;
	}

	public String getParamName() {
		return paramName;
	}
}