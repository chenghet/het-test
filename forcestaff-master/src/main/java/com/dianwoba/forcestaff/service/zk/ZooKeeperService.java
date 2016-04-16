package com.dianwoba.forcestaff.service.zk;

import com.dianwoba.forcestaff.common.Constants;
import com.dianwoba.forcestaff.endpoint.Endpoint;
import org.apache.zookeeper.CreateMode;

public class ZooKeeperService {

    private static final String ROOT_PATH = Constants.FORCESTAFF_NAMESPACE;
    private CuratorService curatorService;

    public ZooKeeperService(String connect, int sessionTimeout) throws ZKException {
        curatorService = new CuratorService(connect, sessionTimeout, Integer.MAX_VALUE);
    }

    /**
     * 通过appKey获取平台节点的路径
     *
     * @param appKey
     * @return
     */
    public static String getPlatformNodeName(String appKey) {
        return String.format("pf-%s", appKey);
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
        curatorService.createNode(platformNodePath, null, CreateMode.PERSISTENT);
        return platformNodePath;
    }

    /**
     * ZK注册Endpoint
     *
     * @param endpoint
     * @return Endpoint路径
     * @throws ZKException
     */
    public String registerEndpoint(Endpoint endpoint) throws ZKException {
        String thisNodePath = getOrCreatePlatformNode(endpoint.getAppKey()) + "/" + endpoint.getId();
        curatorService.createNode(thisNodePath, null, CreateMode.EPHEMERAL);
        return thisNodePath;
    }

    /**
     * ZK取消注册Endpoint
     *
     * @param endpoint
     * @throws Exception
     */
    public void unregisterEndpoint(Endpoint endpoint) throws Exception {
        String thisNodePath = getPlatformNodeName(endpoint.getAppKey()) + "/" + endpoint.getId();
        curatorService.deleteNode(thisNodePath);
    }
}
