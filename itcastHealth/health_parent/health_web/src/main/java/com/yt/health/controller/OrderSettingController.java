package com.yt.health.controller;/*
*包名：com.yt.health.controller
*@author yangtao
*日期：2021-06-28  16:42:06
*/

import com.alibaba.dubbo.config.annotation.Reference;
import com.yt.health.constant.MessageConstant;
import com.yt.health.entity.Result;
import com.yt.health.pojo.OrderSetting;
import com.yt.health.service.OrderSettingService;
import com.yt.health.utils.POIUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ordersetting")
public class OrderSettingController {

    @Reference
    private OrderSettingService orderSettingService;

    @PostMapping("/upload")
    public Result upload(MultipartFile excelFile) throws IOException, ParseException {
        //1.读取excel 内容
        List<String[]> stringList = POIUtils.readExcel(excelFile);
        //2.转成list<OrderSetting>
        List<OrderSetting> orderSettingList = new ArrayList<OrderSetting>();
        OrderSetting orderSetting = null;
        Date orderDate = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(POIUtils.DATE_FORMAT);
        for (String[] strings : stringList) {
            orderDate = simpleDateFormat.parse(strings[0]);
            int number = Integer.parseInt(strings[1]);
            orderSetting = new OrderSetting(orderDate,number);
            orderSettingList.add(orderSetting);
        }
        //3.调用service服务 更新数据库
        orderSettingService.add(orderSettingList);
        //4.返回结果
        return new Result(true, MessageConstant.GET_ORDERSETTING_SUCCESS);
    }

    @GetMapping("/getOrderSettingByMonth")
    public Result getOrderSettingByMonth(String date){
        List<Map> resultList = orderSettingService.getOrderSettingByMonth(date);
        return new Result(true,MessageConstant.GET_ORDERSETTING_SUCCESS,resultList);
    }
}
