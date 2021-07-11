package com.yt.health.service;

import com.yt.health.entity.PageResult;
import com.yt.health.entity.QueryPageBean;
import com.yt.health.exception.HealthException;
import com.yt.health.pojo.CheckItem;

import java.util.List;

public interface CheckItemService  {
    List<CheckItem> findAll();

    void add(CheckItem checkItem);

    PageResult findPage(QueryPageBean queryPageBean);

    void deleteById  (int id)  throws HealthException;

    CheckItem findById(int id);

    void update(CheckItem checkItem);

}
