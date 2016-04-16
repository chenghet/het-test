package com.dianwoba.forcestaff.monitor.service;

import com.dianwoba.forcestaff.common.Constants;
import com.dianwoba.forcestaff.service.zk.CuratorService;
import com.dianwoba.forcestaff.service.zk.ZKException;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.utils.CloseableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by het on 2016/4/7.
 */
@Service
public class ZKService {
    @Autowired
    private CuratorService curatorService;

    private PathChildrenCache rootPathChildCache;

    @PostConstruct
    private void init() throws Exception {
        rootPathChildCache = new PathChildrenCache(curatorService.getCurator(), Constants.FORCESTAFF_NAMESPACE, true);
        rootPathChildCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
    }

    public PathChildrenCache getRootPathCache() {
        return rootPathChildCache;
    }

    public PathChildrenCache getNodePathCache(String path) {
        PathChildrenCache cache = new PathChildrenCache(curatorService.getCurator(), path, true);
        try {
            cache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        } catch (Exception e) {
            throw new ZKException(e);
        }
        return cache;
    }

    @PreDestroy
    public void close() {
        CloseableUtils.closeQuietly(rootPathChildCache);
        curatorService.close();
    }
}
