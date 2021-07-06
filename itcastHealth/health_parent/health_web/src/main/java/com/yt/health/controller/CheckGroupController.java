package com.yt.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yt.health.constant.MessageConstant;
import com.yt.health.entity.PageResult;
import com.yt.health.entity.QueryPageBean;
import com.yt.health.entity.Result;
import com.yt.health.pojo.CheckGroup;
import com.yt.health.pojo.CheckItem;
import com.yt.health.service.CheckGroupService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 包名：com.yt.health.controller
 *
 * @author Yangtao
 * 日期：2021-06-18  00:46
 */
@RestController
@RequestMapping("/checkgroup")
public class CheckGroupController {

    @Reference
    private CheckGroupService checkGroupService;

    @GetMapping("/findAll")
    public Result findAll(){
        List<CheckItem> list = checkGroupService.findAll();
        return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS,list);
    }

    @PostMapping("/add")
    public Result add(@RequestBody CheckGroup checkGroup,Integer[] integers){
        checkGroupService.add(checkGroup,integers);
        return new Result(true,MessageConstant.ADD_CHECKGROUP_SUCCESS);
    }

    @RequestMapping("/findPage")
    public Result findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult pageResult = checkGroupService.findPage(queryPageBean);
        return new Result(true,MessageConstant.QUERY_CHECKGROUP_SUCCESS,pageResult);
    }

    @RequestMapping("/findById")
    public Result findById(int id){
        CheckGroup checkGroup = checkGroupService.findById(id);
        return new Result(true,MessageConstant.QUERY_CHECKGROUP_SUCCESS,checkGroup);
    }

    @RequestMapping("/update")
    public Result update(@RequestBody CheckGroup checkGroup,Integer[] checkitemIds){
        checkGroupService.update(checkGroup,checkitemIds);
        return new Result(true,MessageConstant.EDIT_CHECKGROUP_SUCCESS);
    }

    @RequestMapping("/findCheckItemsByCheckGroupId")
    public Result findCheckItemsByCheckGroupId(int checkGroupId){
        Integer[] integers = checkGroupService.findCheckItemsByCheckGroupId(checkGroupId);
        return new Result(true,MessageConstant.QUERY_CHECKGROUP_SUCCESS,integers);
    }


    @RequestMapping("/delete")
    public Result delete(int id){
        checkGroupService.delete(id);
        return new Result(true,MessageConstant.DELETE_CHECKGROUP_SUCCESS);
    }
}
