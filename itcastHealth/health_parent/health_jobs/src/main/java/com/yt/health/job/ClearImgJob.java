package com.yt.health.job;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yt.health.service.SetmealService;
import com.yt.health.utils.QiNiuUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 包名：com.yt.health.job
 *
 * @author Yangtao
 * 日期：2021-06-27  21:00
 */
/*
 * @description:清理图片的人物类
 * @author: yangtao
 * @date: 2021/6/27 21:01
 * @param: null
 * @return:
 */
@Component("clearImgJob")
public class ClearImgJob {

    @Reference
    private SetmealService setmealService;

    public void clearImg(){
        // 查出7牛上的所有图片
        List<String> imgIn7Niu = new ArrayList<String>(QiNiuUtils.listFile());
        // 查出数据库中的所有图片
        List<String> imgInDb = setmealService.findAllImgs();
        // 7牛的-数据库的 imgIn7Niu剩下的就是要删除的
        // imgIn7Niu.size(7) - 3 = imgIn7Niu.size(4)
        // imgInDb里的对象在 imgIn7Niu 能找到就把这删除
        imgIn7Niu.removeAll(imgInDb);
        // 删除7牛上的垃圾图片
        String[] strings = imgIn7Niu.toArray(new String[]{});
        QiNiuUtils.removeFiles(strings);
    }
}
