package com.yt.health.controller;

import com.aliyuncs.exceptions.ClientException;
import com.yt.health.constant.MessageConstant;
import com.yt.health.constant.RedisMessageConstant;
import com.yt.health.entity.Result;
import com.yt.health.exception.HealthException;
import com.yt.health.utils.SMSUtils;
import com.yt.health.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 包名：com.yt.health.controller
 *
 * @author Yangtao
 * 日期：2021-07-04  21:19
 */
@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {

    @Autowired
    private JedisPool jedisPool;

    @PostMapping("/send4Order")
    public Result send4Order(String telephone){
        Jedis resource = jedisPool.getResource();
        String key = RedisMessageConstant.SENDTYPE_ORDER + ":" + telephone;
        String validateCode = resource.get(key);
        if (null != validateCode) {
           return new Result(false,"验证码已发送，请注意查收！");
        }
        String code = ValidateCodeUtils.generateValidateCode4String(6);

        try {
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,code);
            resource.setex(key,60*10,code);
            resource.close();
            return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return new Result(false,MessageConstant.SEND_VALIDATECODE_FAIL);
    }
}
