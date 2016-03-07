package com.dianwoba.forcestaff.service.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.dianwoba.forcestaff.endpoint.Endpoint;

public class ZooKeeperService {

	private ZooKeeper zk;
	private static final String ROOT_PATH = "/forcestaff";

	public ZooKeeperService(String connect, int sessionTimeout) throws ZKException {
		try {
			zk = new ZooKeeper(connect, sessionTimeout, new Watcher() {
				public void process(WatchedEvent event) {
					System.out.println(event);
				}
			});
			// 工程的根节点
			if (zk.exists(ROOT_PATH, false) == null) {
				zk.create(ROOT_PATH, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
		} catch (Exception e) {
			throw new ZKException(e);
		}
	}

	/**
	 * 构造平台根节点
	 * 
	 * @param appKey
	 * @return
	 * @throws Exception
	 */
	public String getOrCreatePlatformNode(String appKey) throws ZKException {
		String platformNodePath = ROOT_PATH + "/" + getPlatformNodeName(appKey);
		try {
			if (zk.exists(platformNodePath, false) == null) {
				zk.create(platformNodePath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
		} catch (Exception e) {
			throw new ZKException(e);
		}
		// 注册child watcher
		return platformNodePath;
	}

	/**
	 * 通过appKey获取平台节点的路径
	 * 
	 * @param appKey
	 * @return
	 */
	public static String getPlatformNodeName(String appKey) {
		return String.format("pf%s", appKey);
	}

	/**
	 * ZK注册Endpoint
	 *
	 * @param endpoint
	 * @return Endpoint路径
	 * @throws ZKException
	 */
	public String registerEndpoint(Endpoint endpoint) throws ZKException {
		String thisNodePath;
		try {
			thisNodePath = getOrCreatePlatformNode(endpoint.getAppKey()) + "/" + endpoint.getId();
			if (zk.exists(thisNodePath, false) == null) {
				zk.create(thisNodePath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			}
		} catch (Exception e) {
			throw new ZKException();
		}
		return thisNodePath;
	}

	/**
	 * ZK取消注册Endpoint
	 * 
	 * @param endpoint
	 * @throws Exception
	 */
	public void unregisterEndpoint(Endpoint endpoint) throws Exception {
		String parentNodePath = getOrCreatePlatformNode(endpoint.getAppKey());
		String thisNodePath = parentNodePath + "/" + endpoint.getId();
		zk.delete(thisNodePath, -1);
	}
}
