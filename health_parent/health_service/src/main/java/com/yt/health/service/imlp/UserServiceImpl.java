package com.yt.health.service.imlp;

import com.alibaba.dubbo.config.annotation.Service;
import com.yt.health.dao.UserDao;
import com.yt.health.pojo.User;
import com.yt.health.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 包名：com.yt.health.service.imlp
 *
 * @author Yangtao
 * 日期：2021-07-07  20:24
 */

@Service(interfaceClass = UserService.class)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }
}
