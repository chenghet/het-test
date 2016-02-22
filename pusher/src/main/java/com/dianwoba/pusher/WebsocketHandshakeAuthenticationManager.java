package com.dianwoba.pusher;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;

import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebsocketHandshakeAuthenticationManager {

	private static Logger logger = LoggerFactory.getLogger(WebsocketHandshakeAuthenticationManager.class);

	/**
	 * 校验
	 * 
	 * @param req
	 * @return
	 */
	public boolean authenticationCheck(HttpRequest req) {
		try {
			TreeMap<String, String> treeMap = new TreeMap<String, String>();
			String version = getRequiredHeader(req, WebsocketHandshakeHeader.VERSION);
			String appKey = getRequiredHeader(req, WebsocketHandshakeHeader.APP_KEY);
			String timestamp = getRequiredHeader(req, WebsocketHandshakeHeader.TIMESTAMP);
			if (!checkTimestamp(timestamp)) {
				return false;
			}
			String format = getRequiredHeader(req, WebsocketHandshakeHeader.FORMAT);
			if (!checkFormat(format)) {
				return false;
			}
			String sign = getRequiredHeader(req, WebsocketHandshakeHeader.SIGN);

			// 进行sign签名校验
			treeMap.put(WebsocketHandshakeHeader.VERSION.getHeaderKey(), version);
			treeMap.put(WebsocketHandshakeHeader.APP_KEY.getHeaderKey(), appKey);
			treeMap.put(WebsocketHandshakeHeader.TIMESTAMP.getHeaderKey(), timestamp);
			treeMap.put(WebsocketHandshakeHeader.FORMAT.getHeaderKey(), format);
			String clacSign = AuthenticationUtil.sign(treeMap, getAppSecret(appKey));

			if (!sign.equals(clacSign)) {
				return false;
			}
		} catch (Exception e) {
			logger.warn("Websocket握手权限校验失败", e);
			return false;
		}
		return true;
	}

	/**
	 * 校验请求的时间戳与服务器是否误差在10分钟之内
	 * 
	 * @param timestamp
	 * @return
	 */
	private boolean checkTimestamp(String timestamp) {
		if (!NumberUtils.isDigits(timestamp)) {
			return false;
		}
		return System.currentTimeMillis() - Long.valueOf(timestamp) > 600000;
	}

	/**
	 * 校验websocket的format方式，目前只支持json
	 * 
	 * @param format
	 * @return
	 */
	private boolean checkFormat(String format) {
		return "json".equals(format);
	}

	/**
	 * 获取websocket握手所必须的请求头参数
	 * 
	 * @param req HttpRequest
	 * @param headerItem WebsocketHandshakeHeader枚举
	 * @return
	 * @throws AuthenticationException
	 */
	public String getRequiredHeader(HttpRequest req, WebsocketHandshakeHeader headerItem) {
		String headValue = HttpHeaders.getHeader(req, headerItem.getHeaderKey());
		if (StringUtils.isBlank(headValue)) {
			throw new AuthenticationException("Websocket握手请求头校验失败，缺少校验必须项！");
		}
		return headValue;
	}

	public String getAppSecret(String appKey) {
		// FIXME get appSecret
		return null;
	}
}
