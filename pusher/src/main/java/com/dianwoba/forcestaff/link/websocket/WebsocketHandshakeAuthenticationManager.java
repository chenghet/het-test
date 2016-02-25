package com.dianwoba.forcestaff.link.websocket;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianwoba.constants.CommonConstant.YesOrNo;
import com.dianwoba.forcestaff.core.RemoteErrorEnum;
import com.dianwoba.forcestaff.link.auth.AuthenticationException;
import com.dianwoba.forcestaff.link.auth.AuthenticationInfo;
import com.dianwoba.forcestaff.link.auth.AuthenticationManager;
import com.dianwoba.forcestaff.link.auth.AuthenticationUtil;
import com.dianwoba.redcliff.commdb.entity.PlatformShop;
import com.dianwoba.redcliff.commdb.entity.PlatformShopExample;
import com.dianwoba.redcliff.commdb.mapper.PlatformShopMapperExt;

@Service
public class WebsocketHandshakeAuthenticationManager implements AuthenticationManager {

	@Autowired
	private PlatformShopMapperExt platformShopMapper;

	/**
	 * Websocket连接权限校验
	 * 
	 * @param req HttpRequest
	 * @throws AuthenticationException
	 */
	public AuthenticationInfo authenticate(HttpRequest req) {
		String version = getRequiredHeader(req, WebsocketHandshakeHeaderParam.VERSION);

		// appkey
		String appKey = getRequiredHeader(req, WebsocketHandshakeHeaderParam.APP_KEY);
		PlatformShop pshop = getPlatformShop(appKey);
		checkPlatformShop(pshop);

		// timestamp
		String timestamp = getRequiredHeader(req, WebsocketHandshakeHeaderParam.TIMESTAMP);
		checkTimestamp(timestamp);

		// format
		String format = getRequiredHeader(req, WebsocketHandshakeHeaderParam.ACCEPT_FORMAT);
		checkFormat(format);

		// 进行sign签名校验
		String original = getRequiredHeader(req, WebsocketHandshakeHeaderParam.SIGN);
		Map<String, String> map = new HashMap<String, String>();
		map.put(WebsocketHandshakeHeaderParam.VERSION.getParamName(), version);
		map.put(WebsocketHandshakeHeaderParam.APP_KEY.getParamName(), appKey);
		map.put(WebsocketHandshakeHeaderParam.TIMESTAMP.getParamName(), timestamp);
		map.put(WebsocketHandshakeHeaderParam.ACCEPT_FORMAT.getParamName(), format);
		String calced = AuthenticationUtil.sign(map, pshop.getSecret());
		checkSign(original, calced);
		
		AuthenticationInfo info = new AuthenticationInfo();
		info.setAppKey(appKey);
		info.setAppSecret(pshop.getSecret());
		return info;
	}

	/**
	 * 校验请求的时间戳与服务器是否误差在10分钟之内
	 * 
	 * @param timestamp
	 * @throws AuthenticationException
	 */
	private void checkTimestamp(String timestamp) {
		if (!NumberUtils.isDigits(timestamp) || System.currentTimeMillis() - Long.valueOf(timestamp) > 600000) {
			throw new AuthenticationException(RemoteErrorEnum.HANDSHAKE_TIMESTAMP_ILLEGAL.getErrCode(),
					RemoteErrorEnum.HANDSHAKE_TIMESTAMP_ILLEGAL.getMessage());
		}
	}

	/**
	 * 校验websocket的format方式，目前只支持json
	 * 
	 * @param format
	 * @throws AuthenticationException
	 */
	private void checkFormat(String format) {
		if (!"json".equals(format)) {
			throw new AuthenticationException(RemoteErrorEnum.HANDSHAKE_FORMAT_NOT_SUPPORT.getErrCode(),
					RemoteErrorEnum.HANDSHAKE_FORMAT_NOT_SUPPORT.getMessage());
		}
	}

	/**
	 * 校验PlatformShop信息
	 * 
	 * @param pshop
	 * @throws AuthenticationException
	 */
	private void checkPlatformShop(PlatformShop pshop) {
		if (pshop == null) {
			throw new AuthenticationException(RemoteErrorEnum.HANDSHAKE_PLATFORM_NOT_EXIST.getErrCode(),
					RemoteErrorEnum.HANDSHAKE_PLATFORM_NOT_EXIST.getMessage());
		}
		if (pshop.getSpStatus().intValue() != YesOrNo.YES) {
			throw new AuthenticationException(RemoteErrorEnum.HANDSHAKE_PLATFORM_NOT_ACTIVE.getErrCode(),
					RemoteErrorEnum.HANDSHAKE_PLATFORM_NOT_ACTIVE.getMessage());
		}
	}

	/**
	 * 校验签名参数
	 * 
	 * @param original 请求头中的sign
	 * @param calced 服务端计算的sign
	 * @throws AuthenticationException
	 */
	private void checkSign(String original, String calced) {
		if (original == null || !original.equals(calced)) {
			throw new AuthenticationException(RemoteErrorEnum.HANDSHAKE_SIGN_NOT_MATCH.getErrCode(),
					RemoteErrorEnum.HANDSHAKE_SIGN_NOT_MATCH.getMessage());
		}
	}

	/**
	 * 获取websocket握手所必须的请求头参数
	 * 
	 * @param req HttpRequest
	 * @param param WebsocketHandshakeHeader枚举
	 * @return
	 * @throws AuthenticationException
	 */
	public String getRequiredHeader(HttpRequest req, WebsocketHandshakeHeaderParam param) {
		String headValue = HttpHeaders.getHeader(req, param.getParamName());
		if (StringUtils.isBlank(headValue)) {
			throw new AuthenticationException(RemoteErrorEnum.HANDSHAKE_LASK_REQEUIRED_HEADER_PARAM.getErrCode(),
					RemoteErrorEnum.HANDSHAKE_LASK_REQEUIRED_HEADER_PARAM.getMessage());
		}
		return headValue;
	}

	/**
	 * 通过appKey获取PlatformShop
	 * 
	 * @param appKey
	 * @return
	 */
	public PlatformShop getPlatformShop(String appKey) {
		PlatformShopExample ex = new PlatformShopExample();
		ex.createCriteria().andAppKeyEqualTo(appKey);
		List<PlatformShop> list = platformShopMapper.selectByExample(ex);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
}
