package com.yt.health.dao;

import com.yt.health.pojo.User;

public interface UserDao {
    User findByUsername(String username);
}
