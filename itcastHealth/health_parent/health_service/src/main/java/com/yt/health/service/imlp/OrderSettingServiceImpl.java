package com.yt.health.service.imlp;/*
*包名：com.yt.health.service.imlp
*@author yangtao
*日期：2021-06-28  17:01:27
*/

import com.alibaba.dubbo.config.annotation.Service;
import com.yt.health.dao.OrderSettingDao;
import com.yt.health.exception.HealthException;
import com.yt.health.pojo.OrderSetting;
import com.yt.health.service.OrderSettingService;
import com.yt.health.utils.POIUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrderSettingService.class)
public class OrderSettingServiceImpl implements OrderSettingService {

    @Autowired
    private OrderSettingDao orderSettingDao;

    /*
     * @description:批量设置预约信息 ，excel数据和通过date在数据库中查询的数据进行对比
     * @author: yangtao
     * @date: 2021/6/28 17:01
     * @param: orderSettingList
     * @return: void
     */
    @Transactional
    @Override
    public void add(List<OrderSetting> orderSettingList) {
        for (OrderSetting orderSetting : orderSettingList) {
            //1.通过日期判断是否存在预约设置信息
            OrderSetting os = orderSettingDao.findOrderDate(orderSetting.getOrderDate());
            //2.如果存在
            if (null != os) {
                //2.1 如果最大预约数 和 已预约数 大小
                if (orderSetting.getNumber()<os.getReservations()) {
                    //2.1.1 小于 抛出异常
                    throw new HealthException("最大预约数不得小于已预约数");
                }else{
                    //2.1.2 大于 更新可预约数
                     orderSettingDao.updateNumber(orderSetting);
                }
            }else{
                //3.如果不存在，添加预约信息
                 orderSettingDao.add(orderSetting);
            }

        }
    }

    @Override
    public List<Map> getOrderSettingByMonth(String date) {
        //1.设置查询的时间
        String startTime = date+"-01";
        String endTime = date+"-31";
        Map<String,String> map = new HashMap<>();
        map.put("startTime",startTime);
        map.put("endTime",endTime);
        List<OrderSetting> list=orderSettingDao.getOrderSettingByMonth(map);
        List<Map> data = new ArrayList<>();
        // 3.将List<OrderSetting>，组织成List<Map>
        for (OrderSetting orderSetting : list) {
            Map orderSettingMap = new HashMap();
            orderSettingMap.put("date",orderSetting.getOrderDate().getDate());//获得日期（几号）
            orderSettingMap.put("number",orderSetting.getNumber());//可预约人数
            orderSettingMap.put("reservations",orderSetting.getReservations());//已预约人数
            data.add(orderSettingMap);
        }
        return data;
    }
}
