package com.dianwoba.pusher;

public class RemoteExcepiton extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String errorCode;
	private String message;

	public RemoteExcepiton(String errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getMessage() {
		return message;
	}
}
