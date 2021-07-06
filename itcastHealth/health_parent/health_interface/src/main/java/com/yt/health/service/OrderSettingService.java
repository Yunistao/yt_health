package com.yt.health.service;

import com.yt.health.exception.HealthException;
import com.yt.health.pojo.OrderSetting;

import java.util.List;
import java.util.Map;

public interface OrderSettingService {
    void add(List<OrderSetting> orderSettingList) throws HealthException;

    List<Map> getOrderSettingByMonth(String date);
}
