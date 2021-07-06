package com.yt.health.dao;

import com.yt.health.pojo.OrderSetting;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderSettingDao {
    void add(OrderSetting orderSetting);

    OrderSetting findOrderDate(Date orderDate);

    void updateNumber(OrderSetting orderSetting);

    List<OrderSetting> getOrderSettingByMonth(Map<String, String> map);

    void editReservationsByOrderDate(OrderSetting orderSetting);

}
