package com.dianwoba.forcestaff.sdk.domain.resp;

/**
 * Created by het on 2016/4/12.
 */
public class ResponseDTO<T> {

    private String errorCode;
    private String message;
    private T result;
    private boolean success;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
