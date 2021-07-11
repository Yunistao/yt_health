package com.yt.health.dao;

import com.github.pagehelper.Page;
import com.yt.health.pojo.CheckItem;

import java.util.List;

public interface CheckItemDao {
    List<CheckItem> findAll();

    void add(CheckItem checkItem);

    Page<CheckItem> findPage(String queryString);

    int findCountByCheckItemId(int id);

    void deleteById(int id);

    CheckItem findById(int id);

    void update(CheckItem checkItem);
}

