package com.yt.health.service;

import com.yt.health.exception.HealthException;
import com.yt.health.pojo.Order;

import java.util.Map;

public interface OrderService {
    Order submit(Map<String, String> paramMap) throws HealthException;
}
