package com.yt.health.service;

import com.yt.health.entity.PageResult;
import com.yt.health.entity.QueryPageBean;
import com.yt.health.exception.HealthException;
import com.yt.health.pojo.CheckGroup;
import com.yt.health.pojo.CheckItem;

import java.util.List;
import java.util.Map;

public interface CheckGroupService {
    List<CheckItem> findAll();

    void add(CheckGroup checkGroup, Integer[] integers);

    PageResult findPage(QueryPageBean queryPageBean);

    CheckGroup findById(int id);

    void update(CheckGroup checkGroup, Integer[] checkitemIds);

    void delete(int id) throws HealthException;

    Integer[] findCheckItemsByCheckGroupId(int checkGroupId);
}
