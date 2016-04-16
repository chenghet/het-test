package com.dianwoba.forcestaff.sdk.domain.resp;

import com.alibaba.fastjson.JSON;

/**
 * Created by het on 2016/4/12.
 */
public class ResponseAnalyzeUtil {

    public static <T> ResponseDTO content2ResponseDTO(String content, Class<T> clazz) {
        ResponseDTO orig = JSON.parseObject(content, ResponseDTO.class);
        ResponseDTO<T> ret = new ResponseDTO<T>();
        ret.setMessage(orig.getMessage());
        ret.setErrorCode(orig.getErrorCode());
        ret.setSuccess(orig.isSuccess());
        if (orig.getResult() != null) {
            T t = JSON.parseObject((String) orig.getResult(), clazz);
            ret.setResult(t);
        }
        return ret;
    }

}
