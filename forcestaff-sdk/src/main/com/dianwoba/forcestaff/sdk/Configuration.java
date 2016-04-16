package com.dianwoba.forcestaff.sdk;

/**
 * Created by het on 2016/4/10.
 */
public class Configuration {
    public static final String VERSION = "V1";

    private String appKey;
    private String appSecret;

    private String pushServerURI; // 推送服务（WebSocket）的URI
    private String requestServerAddress; // Http请求的地址

    public static String getVERSION() {
        return VERSION;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getRequestServerAddress() {
        return requestServerAddress;
    }

    public void setRequestServerAddress(String requestServerAddress) {
        this.requestServerAddress = requestServerAddress;
    }

    public String getPushServerURI() {
        return pushServerURI;
    }

    public void setPushServerURI(String pushServerURI) {
        this.pushServerURI = pushServerURI;
    }
}
