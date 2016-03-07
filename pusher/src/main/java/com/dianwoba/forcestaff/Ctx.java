package com.dianwoba.forcestaff;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;

import com.dianwoba.forcestaff.endpoint.Endpoint;
import com.dianwoba.forcestaff.service.zk.ZKException;
import com.dianwoba.forcestaff.service.zk.ZooKeeperService;

public class Ctx {

	private Logger logger = org.slf4j.LoggerFactory.getLogger(Ctx.class);
	private Lock endpointsSync = new ReentrantLock();
	private Map<String, Endpoint> endpoints;
	private ZooKeeperService zkService;

	public Ctx(SystemConfig config) throws ZKException {
		endpoints = new HashMap<String, Endpoint>();
		zkService = new ZooKeeperService(config.getZkConnect(), config.getZkSessionTimeoutMs());
	}

	/**
	 * 注册客户端
	 * 
	 * @param endpoint
	 */
	public void registerEndpoint(Endpoint endpoint) {
		endpointsSync.lock();
		try {
			endpoints.put(endpoint.getId(), endpoint);
			try {
				zkService.registerEndpoint(endpoint);
			} catch (Exception e) {
				logger.warn("register endpoint to zookeeper error.", e);
			}
		} finally {
			endpointsSync.unlock();
		}
	}

	/**
	 * 注销客户端
	 * 
	 * @param endpoint
	 * @return
	 */
	public Endpoint unregisterEndpoint(Endpoint endpoint) {
		endpointsSync.lock();
		try {
			try {
				zkService.unregisterEndpoint(endpoint);
			} catch (Exception e) {
				logger.warn("unregister endpoint from zookeeper error.", e);
			}
			return endpoints.remove(endpoint.getId());
		} finally {
			endpointsSync.unlock();
		}
	}

	/**
	 * 根据appkey和remote ip地址查找Endpoint
	 * 
	 * @param appKey
	 * @param addr
	 * @return
	 */
	public Endpoint findEndpoint(String appKey, String addr) {
		Endpoint endpoint = null;
		String id = Endpoint.getEndpointId(appKey, addr);
		endpointsSync.lock();
		try {
			endpoint = endpoints.get(id);
		} finally {
			endpointsSync.unlock();
		}
		return endpoint;
	}
	
	public void shutdown() {
		endpointsSync.lock();
		try {
			Collection<Endpoint> eps = endpoints.values();
			for (Endpoint endpoint : eps) {
				endpoint.shutdown();
			}
		} finally {
			endpointsSync.unlock();
		}
	}
}
