package com.yt.health.dao;

import com.github.pagehelper.Page;
import com.yt.health.pojo.CheckGroup;
import com.yt.health.pojo.CheckItem;

import java.util.List;
import java.util.Map;

public interface CheckGroupDao {
    List<CheckItem> findAll();

    void add(CheckGroup checkGroup);

    //根据gid 和 itemid 添加 
    void addCheckGroupCheckItem(Map map);

    Page<CheckGroup> findPage(String queryString);

    CheckGroup findById(int id);

    void deleteCheckGroupCheckItem(Integer id);

    void update(CheckGroup checkGroup);

    void delete(int id);

    int findSetmealCountByCheckGroupId(int id);

    Integer[] findCheckItemsByCheckGroupId(int checkGroupId);
}
