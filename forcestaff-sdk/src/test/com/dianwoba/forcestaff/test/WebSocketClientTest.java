package com.dianwoba.forcestaff.test;

import com.alibaba.fastjson.JSON;
import com.dianwoba.forcestaff.message.Message;
import com.dianwoba.forcestaff.sdk.Configuration;
import com.dianwoba.forcestaff.sdk.link.MessageHandler;
import com.dianwoba.forcestaff.sdk.link.ws.NettyClient;

/**
 * Created by het on 2016/4/14.
 */
public class WebSocketClientTest {
    public static void main(String[] args) throws Exception {
        Configuration cfg = new Configuration();
        cfg.setAppKey("10001");
        cfg.setAppSecret("2f3f8015a5446f48326349e243206088");
        cfg.setPushServerURI("ws://localhost:45678/ws");
        NettyClient nettyClient = new NettyClient(cfg, new MessageHandler() {
            public void handleMessage(Message message) {
                System.out.println(JSON.toJSONString(message));
            }
        });
        nettyClient.connect();
    }
}
