package com.dianwoba.pusher;

public enum RemoteErrorEnum {
	
	HANDSHAKE_LASK_REQEUIRED_HEADER_PARAM("1001", "Websocket握手失败，请求缺少必须请求头参数。"),
	HANDSHAKE_TIMESTAMP_ILLEGAL("1002", "Websocket握手失败，时间戳不合法或请求已过时。"),
	HANDSHAKE_FORMAT_NOT_SUPPORT("1003", "Websocket握手失败，不支持客户端接受的格式化方式。"),
	HANDSHAKE_PLATFORM_NOT_EXIST("1009", "Websocket握手失败，appKey对应的平台不存在。"),
	HANDSHAKE_PLATFORM_NOT_ACTIVE("1010", "Websocket握手失败，该平台已经禁用。"),
	HANDSHAKE_SIGN_NOT_MATCH("1011", "Websocket握手失败，签名校验失败。"),
	;
	
	private String errCode;
	private String message;

	RemoteErrorEnum(String errCode, String message) {
		this.errCode = errCode;
		this.message = message;
	}

	public String getErrCode() {
		return errCode;
	}

	public String getMessage() {
		return message;
	}
}