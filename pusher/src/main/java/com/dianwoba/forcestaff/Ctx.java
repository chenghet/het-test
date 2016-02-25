package com.dianwoba.forcestaff;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.dianwoba.forcestaff.endpoint.Endpoint;

public class Ctx {

	private Lock endpointsSync = new ReentrantLock();
	private Map<String, Endpoint> endpoints;

	public Ctx() {
		endpoints = new HashMap<String, Endpoint>();
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
		String id = appKey + "#" + addr;
		endpointsSync.lock();
		try {
			endpoint = endpoints.get(id);
		} finally {
			endpointsSync.unlock();
		}
		return endpoint;
	}

	static int a() {
		int a = 1;
		try {
			System.out.println("before return : " + a);
			return a;
		} finally {
			a = 2;
		}
	}

	public static void main(String[] args) {
		System.out.println(a());
	}
}
