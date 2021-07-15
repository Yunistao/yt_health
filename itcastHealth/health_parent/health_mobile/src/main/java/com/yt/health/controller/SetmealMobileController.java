package com.yt.health.controller;/*
*包名：com.yt.health.controller
*@author yangtao
*日期：2021-07-01  15:32:38
*/

import com.alibaba.dubbo.config.annotation.Reference;
import com.yt.health.constant.MessageConstant;
import com.yt.health.entity.Result;
import com.yt.health.pojo.Setmeal;
import com.yt.health.service.SetmealService;
import com.yt.health.utils.QiNiuUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealMobileController {

    @Reference
    private SetmealService setmealService;

    @PostMapping("/getSetmeal")
    public Result getSetmeal(){
        List<Setmeal> list = setmealService.findAllSetmeal();
/*        for (Setmeal setmeal : list) {
            setmeal.setImg(QiNiuUtils.DOMAIN+setmeal.getImg());
        }*/
        return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS,list);
    }

    @PostMapping("/findById")
    public Result findById(Integer id){
        Setmeal setmeal = setmealService.findSetmealById(id);
        setmeal.setImg(QiNiuUtils.DOMAIN+setmeal.getImg());
        return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);
    }

    /**
     * 查询套餐详情
     */
    @GetMapping("/findDetailById2")
    public Result findDetailById2(int id){
        // 调用服务查询详情
        Setmeal setmeal = setmealService.findDetailById2(id);
        // 设置图片的完整路径
        setmeal.setImg(QiNiuUtils.DOMAIN + setmeal.getImg());
        return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);
    }
}
