package com.yt.health.controller;

import com.yt.health.entity.Result;
import com.yt.health.exception.HealthException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
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

    /**
     *  info:  打印日志，记录流程性的内容
     *  debug: 记录一些重要的数据 id, orderId, userId
     *  error: 记录异常的堆栈信息，代替e.printStackTrace();
     *  工作中不能有System.out.println(), e.printStackTrace();
     */
    private static final Logger log = LoggerFactory.getLogger(HealthException.class);

    //捕获指定类型的异常
    @ExceptionHandler(HealthException.class)
    public Result handleHealthException(HealthException he){
        return new Result(false,he.getMessage());
    }

   @ExceptionHandler(Exception.class)
    public Result handleException(Exception ex){
        log.error("出现异常",ex);
        return new Result(false, "发生未知错误，操作失败，请联系管理员");
    }

    /**
     * 密码错误
     * @param he
     * @return
     */
    @ExceptionHandler(BadCredentialsException.class)
    public Result handBadCredentialsException(BadCredentialsException he){
        return new Result(false, "用户名或密码错误");
    }

    /**
     * 用户名不存在
     * @param he
     * @return
     */
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public Result handInternalAuthenticationServiceException(InternalAuthenticationServiceException he){
        return new Result(false, "用户名不存在");
    }


    /**
     * 用户名无权限
     * @param e
     * @return
     */
//   注意不要倒错包 import org.springframework.security.access.AccessDeniedException;
    @ExceptionHandler(AccessDeniedException.class)
    public Result handleAccessDeniedException(AccessDeniedException e){
        return new Result(false, "权限不足");
    }


}
