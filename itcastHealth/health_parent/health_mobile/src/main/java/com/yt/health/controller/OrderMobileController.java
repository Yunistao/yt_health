package com.yt.health.controller;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.yt.health.constant.MessageConstant;
import com.yt.health.constant.RedisMessageConstant;
import com.yt.health.entity.Result;
import com.yt.health.pojo.Order;
import com.yt.health.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;

/**
 * 包名：com.yt.health.controller
 *
 * @author Yangtao
 * 日期：2021-07-04  19:11
 */
@RequestMapping("order")
@RestController
public class OrderMobileController {

    @Autowired
    private JedisPool jedisPool;

    @Reference
    private OrderService orderService;

    @GetMapping("/submit")
    public Result submit(@RequestBody Map<String,String>  paramMap){
        //1.获得redis连接
        Jedis jedis = jedisPool.getResource();
        //2.获得前端输入的验证码
        String validateCode = paramMap.get("validateCode");
        //3.获得redis中的验证码
        String telephone = paramMap.get("telephone");
        String key = RedisMessageConstant.SENDTYPE_ORDER + ":" + telephone;
        String codeInRedis = jedis.get(key);
        //4.判断redis中的验证码是不否存在，不存在，return false
        if (StringUtils.isEmpty(codeInRedis)) {
            return new Result(false, "请重新获取验证码！");
        }
        //5.判断两个验证码是否相等，不相等，return false
        if (!codeInRedis.equals(validateCode)) {
            return new Result(false,MessageConstant.VALIDATECODE_ERROR);
        }
        //6.调用orderservice服务端的submit方法
        paramMap.put("orderType",Order.ORDERTYPE_WEIXIN);
        Order order = orderService.submit(paramMap);
        return new Result(true,MessageConstant.GET_ORDERSETTING_SUCCESS,order);
    }
}
