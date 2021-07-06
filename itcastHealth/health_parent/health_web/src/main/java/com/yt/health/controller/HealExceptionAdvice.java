package com.yt.health.controller;

import com.yt.health.entity.Result;
import com.yt.health.exception.HealthException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



/**
 * 包名：com.yt.health.controller
 *
 * @author Yangtao
 * 日期：2021-06-17  22:52
 */
//全局异常处理
@RestControllerAdvice
public class HealExceptionAdvice {
    /*
     * @description:自定义招出的异常处理
     * @author
     * @date: 2021/6/17 22:52
     * @param: null
     * @return:
     */
    private static final Logger log = LoggerFactory.getLogger(HealthException.class);

    //捕获指定类型的异常
    @ExceptionHandler(HealthException.class)
    public Result handleHealthException(HealthException he){
        log.error("出现异常",he);
        return new Result(false,he.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception ex){
        log.error("出现异常",ex);
        return new Result(false, "发生未知错误，操作失败，请联系管理员");
    }
}
