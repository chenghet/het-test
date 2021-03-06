package com.dianwoba.forcestaff.link.auth;

import com.dianwoba.forcestaff.core.RemoteExcepiton;

public class AuthenticationException extends RemoteExcepiton {
    private static final long serialVersionUID = 1L;

    public AuthenticationException(String errorCode, String message) {
        super(errorCode, message);
    }

    @Override
    public String toString() {
        return String.format("权限校验失败：%s，%s", getErrorCode(), getMessage());
    }
}
