package com.yt.health.controller;

import com.yt.health.constant.MessageConstant;
import com.yt.health.entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 包名：com.yt.health.controller
 *
 * @author Yangtao
 * 日期：2021-07-07  20:42
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/getUsername")
    public Result getUsername(){
        org.springframework.security.core.userdetails.User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new Result(true, MessageConstant.GET_USERNAME_SUCCESS,loginUser.getUsername());
    }
}
