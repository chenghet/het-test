package com.dianwoba.forcestaff.service.zk;

/**
 * Created by het on 2016/4/6.
 */
public class ZKException extends RuntimeException {

    public ZKException(Exception cause) {
        super(cause);
    }

    public ZKException(String message, Exception cause) {
        super(message, cause);
    }
}
