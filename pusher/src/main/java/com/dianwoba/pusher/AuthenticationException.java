package com.dianwoba.pusher;

public class AuthenticationException extends SecurityException {
	private static final long serialVersionUID = 1L;

	public AuthenticationException() {
		super();
	}

	public AuthenticationException(String causeReason) {
		super(causeReason);
	}
}
