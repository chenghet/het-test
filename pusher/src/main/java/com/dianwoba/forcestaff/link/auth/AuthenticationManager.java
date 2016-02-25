package com.dianwoba.forcestaff.link.auth;

import io.netty.handler.codec.http.HttpRequest;

public interface AuthenticationManager {

	/**
	 * 权限校验
	 * 
	 * @param req
	 */
	public AuthenticationInfo authenticate(HttpRequest req);
}
