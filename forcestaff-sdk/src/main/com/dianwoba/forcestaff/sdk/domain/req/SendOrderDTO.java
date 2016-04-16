package com.dianwoba.forcestaff.sdk.domain.req;

import java.util.List;

/**
 * Created by het on 2016/4/12.
 */
public class SendOrderDTO extends BaseRequestDTO {

    public int service_type;
    public String order_original_id;
    public Long order_create_time;
    public String order_remark;
    public Integer order_price;
    public String city_code;
    public String seller_id;
    public String seller_name;
    public String seller_mobile;
    public String seller_addreess;
    public double seller_lat;
    public double seller_lng;
    public String consignee_name;
    public String consignee_mobile;
    public String consignee_address;
    public double consignee_lat;
    public double consignee_lng;
    public Integer money_rider_needpaid;
    public Integer money_rider_prepaid;
    public Integer money_rider_charge;
    public Long time_ready_for_deliver;
    public Integer time_wait_at_seller;
    public Long time_expected_arrival;
    public Integer delivery_fee_from_seller;
    public List<PlaceOrderItemDTO> items;
    public String callback;


    public static class PlaceOrderItemDTO {
        public String item_name;
        public String unit;
        public Integer quantity;
        public Long unit_price;
        public Long discount_price;
        public Long production_time;
    }
}
