package com.yt.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yt.health.constant.MessageConstant;
import com.yt.health.entity.PageResult;
import com.yt.health.entity.QueryPageBean;
import com.yt.health.entity.Result;
import com.yt.health.pojo.CheckGroup;
import com.yt.health.pojo.Setmeal;
import com.yt.health.service.SetmealService;
import com.yt.health.utils.QiNiuUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.POST;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 包名：com.yt.health.controller
 *
 * @author Yangtao
 * 日期：2021-06-24  21:35
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Reference
    private SetmealService setmealService;

    @PostMapping("/picUpload")
    public Result upload(MultipartFile imgFile){
        //- 获取原有图片名称，截取到后缀名
        String originalFilename = imgFile.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        //- 生成唯一文件名，拼接后缀名
        String filename = UUID.randomUUID() + extension;
        //- 调用七牛上传文件
        try {
            QiNiuUtils.uploadViaByte(imgFile.getBytes(), filename);
            //- 返回数据给页面
            //{
            //    flag:
            //    message:
            //    data:{
            //        imgName: 图片名,
            //        domain: QiNiuUtils.DOMAIN
            //    }
            //}
            Map<String,String> map = new HashMap<String,String>();
            map.put("imgName",filename);
            map.put("domain", QiNiuUtils.DOMAIN);
            return new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS,map);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
    }

    @GetMapping("/findAll")
    public Result findAll(){
        List<CheckGroup> list = setmealService.findAll();
        return new Result(true,MessageConstant.QUERY_CHECKGROUP_SUCCESS,list);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Setmeal setmeal,Integer[] checkgroupIds){
        setmealService.add(setmeal,checkgroupIds);
        return new Result(true,MessageConstant.ADD_SETMEAL_SUCCESS);
    }

    @PostMapping("/findPage")
    public Result findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult pageResult = setmealService.findPage(queryPageBean);
        return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,pageResult);
    }

    @GetMapping("/findById")
    public Result findById(int id){
        Setmeal setmeal = setmealService.findById(id);
        Map<String,Object> map = new HashMap<>();
        map.put("setmeal",setmeal);
        map.put("imgUrl",QiNiuUtils.DOMAIN);
        return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,map);
    }

    @GetMapping("/findCheckGroupsBySetmealId")
    public Result findCheckGroupsBySetmealId(int setmealId){
        Integer[] integers = setmealService.findCheckGroupsBySetmealId(setmealId);
        return new Result(true,MessageConstant.QUERY_CHECKGROUP_SUCCESS,integers);
    }

    @PostMapping("/update")
    public Result update(Integer[] checkGroupIds,@RequestBody Setmeal setmeal){
        //此处犯了错误，json格式的数据忘了添加RequestBody标签
        setmealService.update(checkGroupIds,setmeal);
        return new Result(true,MessageConstant.EDIT_COMBO_SUCCESS);
    }

    @PostMapping("/delete")
    public Result delete(Integer id){
        setmealService.deleteById(id);
        return new Result(true,MessageConstant.DELETE_COMBO_SUCCESS);
    }

}
