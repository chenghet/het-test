package com.dianwoba.forcestaff.service.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.EnsurePath;

import java.util.List;


/**
 * Created by het on 2016/4/7.
 */
public class Cache {


    public static PathChildrenCache pathChildrenCache(CuratorFramework client, String path, Boolean cacheData) throws Exception {
        final PathChildrenCache cached;
        cached = new PathChildrenCache(client, path, cacheData);
        cached.getListenable().addListener(new PathChildrenCacheListener() {
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                PathChildrenCacheEvent.Type eventType = event.getType();
                switch (eventType) {
                    case CONNECTION_RECONNECTED:
                        cached.rebuild();
                        break;
                    case CONNECTION_SUSPENDED:
                        break;
                    case CONNECTION_LOST:
                        System.out.println("Connection error,waiting...");
                        break;
                    default:
                        System.out.println("PathChildrenCache changed : {path:" + event.getData().getPath() + " data:" +
                                new String(event.getData().getData()) + "}");
                }
            }
        });
        return cached;
    }


    public static NodeCache nodeCache(CuratorFramework client, String path) {
        final NodeCache cache = new NodeCache(client, path);
        cache.getListenable().addListener(new NodeCacheListener() {
            public void nodeChanged() throws Exception {
                System.out.println("NodeCache changed, data is: " + new String(cache.getCurrentData().getData()));
            }
        });
        return cache;
    }


    public static void main(String[] args) throws Exception {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1", retryPolicy);
        client.start();

        EnsurePath ensurePath = client.newNamespaceAwareEnsurePath("/create/test");
        ensurePath.ensure(client.getZookeeperClient());

        /**
         * pathChildrenCache
         */
        PathChildrenCache cache = pathChildrenCache(client, "/create", true);
        cache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        List<ChildData> datas = cache.getCurrentData();
        for (ChildData data : datas) {
            System.out.println("pathcache:{" + data.getPath() + ":" + new String(data.getData()) + "}");
        }


        /**
         *	NodeCache
         */
        NodeCache nodeCache = nodeCache(client, "/create/test");
        nodeCache.start(true);
        client.setData().forPath("/create/test", "1111".getBytes());
        System.out.println(new String(nodeCache.getCurrentData().getData()));
        Thread.sleep(10000);
    }
}