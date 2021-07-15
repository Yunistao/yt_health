package com.yt.health.service;

import com.yt.health.pojo.User;

public interface UserService {
    User findByUsername(String username);
}
