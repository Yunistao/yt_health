package com.yt.health.dao;

import com.yt.health.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OrderDao {
    public void add(Order order);
    public List<Order> findByCondition(Order order);
    public Map findById4Detail(Integer id);
    public Integer findOrderCountByDate(String date);
    public Integer findOrderCountAfterDate(String date);
    public Integer findVisitsCountByDate(String date);
    public Integer findVisitsCountAfterDate(String date);
    public List<Map<String,Object>> findHotSetmeal();

    //本周预约数
    int findOrderCountBetweenDate(@Param("monday") String monday,@Param("sunday") String sunday);


}
