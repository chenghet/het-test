package com.dianwoba.forcestaff.sdk.domain.req;

/**
 * Created by het on 2016/4/13.
 */
public enum AuthenticationParamEn {
    pk, timestamp, sig, format("json");

    private Object defaultValue;

    AuthenticationParamEn() {
    }

    AuthenticationParamEn(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
}
