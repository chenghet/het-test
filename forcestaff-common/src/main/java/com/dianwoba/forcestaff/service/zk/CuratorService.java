package com.dianwoba.forcestaff.service.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.List;

/**
 * Created by het on 2016/4/6.
 */
public class CuratorService {

    public static final Logger log = LoggerFactory.getLogger(CuratorService.class);
    private CuratorFramework curator;

    public CuratorService(String connectString, int sessionTimeoutMs, int connectionTimeoutMs) {
        curator = CuratorFrameworkFactory.builder()
                .connectString(connectString).sessionTimeoutMs(sessionTimeoutMs)
                .connectionTimeoutMs(connectionTimeoutMs).retryPolicy(new RetryOneTime(500)).build();
        curator.start();
    }

    public static void main(String[] args) throws Exception {
        CuratorService service = new CuratorService("localhost", 500, 500);
        service.createNode("/hello/hi", "hello".getBytes(), CreateMode.EPHEMERAL);
        service.getChild("/hello");
    }

    public CuratorFramework getCurator() {
        return curator;
    }

    /**
     * 创建节点
     *
     * @param path
     * @param mode
     */
    public void createNode(String path, byte[] content, CreateMode mode) {
        try {
            if (curator.checkExists().forPath(path) == null) {
                curator.create().creatingParentsIfNeeded().withMode(mode)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).forPath(path, content);
            }
            log.warn(String.format("node existed for path %s", path));
        } catch (Exception e) {
            throw new ZKException(e);
        }
    }

    /**
     * 更新节点信息，如果
     *
     * @param path
     * @param content
     */
    public void updateNode(String path, byte[] content) {
        try {
            if (curator.checkExists().forPath(path) != null) {
                curator.setData().forPath(path, content);
            }
            log.warn(String.format("node existed for path %s", path));
        } catch (Exception e) {
            throw new ZKException(e);
        }
    }

    /**
     * 删除节点
     *
     * @param path
     */
    public void deleteNode(String path) {
        try {
            if (curator.checkExists().forPath(path) != null) {
                curator.delete().withVersion(-1).forPath(path);
            }
            log.warn(String.format("no node exists for path %s", path));
        } catch (Exception e) {
            throw new ZKException(e);
        }
    }

    /**
     * 获取Node的数据
     *
     * @param path
     * @return
     */
    public byte[] getNodeData(String path) {
        try {
            if (curator.checkExists().forPath(path) != null) {
                return curator.getData().forPath(path);
            }
            log.warn(String.format("no node exists for path %s", path));
            return null;
        } catch (Exception e) {
            throw new ZKException(e);
        }
    }

    /**
     * 获取ZNode的Stat信息
     *
     * @param path
     * @return
     */
    public Stat nodeStat(String path) {
        try {
            return curator.checkExists().forPath(path);
        } catch (Exception e) {
            log.error("error happened.", e);
            return null;
        }
    }

    public void getChild(String path) {
        PathChildrenCache cache = new PathChildrenCache(this.curator, path, true);
        try {
            cache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<ChildData> childDatas = cache.getCurrentData();
        for (ChildData data : childDatas) {
            System.out.println(data);
        }
    }

    @PreDestroy
    public void close() {
        curator.close();
    }
}