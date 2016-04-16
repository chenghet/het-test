package com.dianwoba.forcestaff.sdk.link.http;

import com.alibaba.fastjson.JSON;
import com.dianwoba.forcestaff.sdk.Configuration;
import com.dianwoba.forcestaff.sdk.domain.req.*;
import com.dianwoba.forcestaff.sdk.domain.resp.OrderInfoDTO;
import com.dianwoba.forcestaff.sdk.domain.resp.ResponseAnalyzeUtil;
import com.dianwoba.forcestaff.sdk.domain.resp.ResponseDTO;
import com.dianwoba.forcestaff.sdk.domain.resp.RiderPositionDTO;
import com.dianwoba.forcestaff.sdk.util.HttpClientUtil;
import com.dianwoba.forcestaff.util.AuthenticationUtil;

import java.util.Map;

/**
 * Created by het on 2016/4/12.
 */
public final class DwdClient {

    public static final String REQUEST_PATH_ORDER_SEND = "/order-send.json";
    public static final String REQUEST_PATH_ORDER_GET = "/order-get.json";
    public static final String REQUEST_PATH_ORDER_CANCEL = "/order-cancel.json";
    public static final String REQUEST_PATH_ORDER_RIDER_POSITION = "/order-rider-position.json";
    public static final String REQUEST_PATH_ORDER_UPDATE = "/order-rider-position.json";

    private Configuration config;

    public DwdClient(Configuration config) {
        this.config = config;
    }

    /**
     * 发送订单
     *
     * @param sendOrderDTO
     * @return
     */
    public ResponseDTO<OrderInfoDTO> sendOrder(SendOrderDTO sendOrderDTO) {
        String uri = config.getRequestServerAddress() + REQUEST_PATH_ORDER_SEND;
        Map<String, Object> params = bean2ParamMap(sendOrderDTO);
        String resp = HttpClientUtil.get(uri, params);
        return ResponseAnalyzeUtil.content2ResponseDTO(resp, OrderInfoDTO.class);
    }

    /**
     * 获取配送员位置信息
     *
     * @param getRiderPositionDTO
     * @return
     */
    public ResponseDTO<RiderPositionDTO> getRiderPosition(GetRiderPositionDTO getRiderPositionDTO) {
        String uri = config.getRequestServerAddress() + REQUEST_PATH_ORDER_RIDER_POSITION;
        Map<String, Object> params = bean2ParamMap(getRiderPositionDTO);
        String resp = HttpClientUtil.get(uri, params);
        return ResponseAnalyzeUtil.content2ResponseDTO(resp, RiderPositionDTO.class);
    }

    /**
     * 取消订单
     *
     * @param cancelOrderDTO
     * @return
     */
    public ResponseDTO<OrderInfoDTO> cancelOrder(CancelOrderDTO cancelOrderDTO) {
        String uri = config.getRequestServerAddress() + REQUEST_PATH_ORDER_CANCEL;
        Map<String, Object> params = bean2ParamMap(cancelOrderDTO);
        String resp = HttpClientUtil.get(uri, params);
        return ResponseAnalyzeUtil.content2ResponseDTO(resp, OrderInfoDTO.class);
    }

    /**
     * 取消订单
     *
     * @param updateOrderDTO
     * @return
     */
    public ResponseDTO<OrderInfoDTO> updateOrder(UpdateOrderDTO updateOrderDTO) {
        String uri = config.getRequestServerAddress() + REQUEST_PATH_ORDER_UPDATE;
        Map<String, Object> params = bean2ParamMap(updateOrderDTO);
        String resp = HttpClientUtil.get(uri, params);
        return ResponseAnalyzeUtil.content2ResponseDTO(resp, OrderInfoDTO.class);
    }

    /**
     * 查询订单
     *
     * @param getOrderDTO
     * @return
     */
    public ResponseDTO<OrderInfoDTO> getOrderInfo(GetOrderDTO getOrderDTO) {
        String uri = config.getRequestServerAddress() + REQUEST_PATH_ORDER_GET;
        Map<String, Object> params = bean2ParamMap(getOrderDTO);
        String resp = HttpClientUtil.get(uri, params);
        return ResponseAnalyzeUtil.content2ResponseDTO(resp, OrderInfoDTO.class);
    }

    /**
     * 把请求对象转化为请求参数
     *
     * @param t
     * @param <T>
     * @return
     */
    public <T extends BaseRequestDTO> Map<String, Object> bean2ParamMap(T t) {
        Map<String, Object> map = (Map<String, Object>) JSON.toJSON(t);
        map.remove(AuthenticationParamEn.sig.name());
        String sig = AuthenticationUtil.sign(map, config.getAppSecret());
        t.sig = sig;
        map.put(AuthenticationParamEn.sig.name(), sig);
        return map;
    }
}
