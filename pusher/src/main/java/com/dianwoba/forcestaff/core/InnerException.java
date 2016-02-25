package com.dianwoba.forcestaff.core;

public class InnerException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InnerException() {
		super();
	}

	public InnerException(String message) {
		super(message);
	}

	public InnerException(String message, Throwable cause) {
		super(message, cause);
	}
}
