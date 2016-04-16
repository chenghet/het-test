package com.dianwoba.forcestaff.monitor.web.controller;

import com.dianwoba.forcestaff.common.Constants;
import com.dianwoba.forcestaff.monitor.service.ZKService;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by het on 2016/4/6.
 */
@Controller
public class MoniterController {

    @Autowired
    private ZKService zkService;

    /**
     * 查看app列表
     *
     * @return
     */
    @RequestMapping("/apps")
    public
    @ResponseBody
    Object listApps() {
        PathChildrenCache cache = zkService.getRootPathCache();
        List<ChildData> childDatas = cache.getCurrentData();
        return childDatas;
    }


    /**
     * 查看appKey下面的信息<br/>
     * 1、endpoint列表
     * 2、消息队列的情况
     *
     * @param appKey
     * @return
     */
    @RequestMapping("/apps/{appKey}")
    @ResponseBody
    public ModelMap appDetail(@PathVariable("appKey") String appKey) {
        String path = Constants.FORCESTAFF_NAMESPACE + "/" + String.format("ps-%s", appKey);
        PathChildrenCache cache = zkService.getNodePathCache(path);
        ModelMap map = new ModelMap();
        map.addAttribute("endpoints", cache.getCurrentData());
        return map;
    }
}