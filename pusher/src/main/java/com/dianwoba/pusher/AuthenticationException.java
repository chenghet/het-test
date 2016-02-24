package com.dianwoba.pusher;

public class AuthenticationException extends RemoteExcepiton {
	private static final long serialVersionUID = 1L;

	public AuthenticationException(String errorCode, String message) {
		super(errorCode, message);
	}
}
