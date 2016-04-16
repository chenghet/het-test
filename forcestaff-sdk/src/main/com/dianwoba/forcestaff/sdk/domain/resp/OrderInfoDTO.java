package com.dianwoba.forcestaff.sdk.domain.resp;

import java.util.List;

/**
 * Created by het on 2016/4/12.
 */
public class OrderInfoDTO {

    public String dwd_order_id;
    public String original_order_id;
    public int order_status;
    public Long time_statue_update;
    public int service_type;
    public String abnormal_reason;
    public String rider_code;
    public String rider_name;
    public Double rider_lat;
    public Double rider_lng;
    public String rider_mobile;
    public Long time_position_update;
    public Long time_estimated_pickup;
    public List<LogisticInfoDTO> logistic_info;

    public static class LogisticInfoDTO {
        public int order_statue;
        public Long time_status_update;
    }
}
