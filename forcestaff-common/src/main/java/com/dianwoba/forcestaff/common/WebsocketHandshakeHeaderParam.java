package com.dianwoba.forcestaff.common;

public enum WebsocketHandshakeHeaderParam {
    VERSION("version"),
    APP_KEY("appKey"),
    TIMESTAMP("timestamp"),
    SIGN("sig"),
    ACCEPT_FORMAT("accept-format", "json");

    private String paramName;
    private String defaultValue;

    WebsocketHandshakeHeaderParam(String paramName) {
        this.paramName = paramName;
    }

    WebsocketHandshakeHeaderParam(String paramName, String defaultValue) {
        this.paramName = paramName;
        this.defaultValue = defaultValue;
    }

    public String getParamName() {
        return paramName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}