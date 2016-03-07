package com.dianwoba.forcestaff.service.zk;

public class ZKException extends Exception {
	private static final long serialVersionUID = 1L;

	public ZKException() {
		super();
	}

	public ZKException(Throwable t) {
		super(t);
	}

	public ZKException(String message, Throwable t) {
		super(message, t);
	}
}
