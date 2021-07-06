package com.yt.health.service;

import com.yt.health.entity.PageResult;
import com.yt.health.entity.QueryPageBean;
import com.yt.health.exception.HealthException;
import com.yt.health.pojo.CheckGroup;
import com.yt.health.pojo.Setmeal;

import java.util.List;

public interface SetmealService {
    List<CheckGroup> findAll();

    void add(Setmeal setmeal,Integer[] checkgroupIds);

    PageResult findPage(QueryPageBean queryPageBean);

    Setmeal findById(int id);

    Integer[] findCheckGroupsBySetmealId(int id);

    void update(Integer[] checkGroupIds, Setmeal setmeal);

    void deleteById(Integer id) throws HealthException;

    List<String> findAllImgs();

    List<Setmeal> findAllSetmeal();

    Setmeal findSetmealById(Integer id) throws HealthException;

    Setmeal findDetailById2(int id);
}
