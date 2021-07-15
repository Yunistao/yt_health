package com.yt.health.security;/*
*包名：com.yt.health.security
*@author yangtao
*日期：2021-07-07  09:43:40
*/

import com.alibaba.dubbo.config.annotation.Reference;
import com.yt.health.pojo.Permission;
import com.yt.health.pojo.Role;
import com.yt.health.pojo.User;
import com.yt.health.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class SpringSecurityUserService implements UserDetailsService {

    @Reference
    private UserService userService;

    /*
     * @description:提供用户登录信息
     * @author: yangtao
     * @date: 2021/7/7 9:50
     * @param: username
     * @return: org.springframework.security.core.userdetails.UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据t_user => t_user_role => t_role => t_role_permission => t_permission
        //找出用户所拥有的角色，和角色所属的所有权限，并添加至权限集合（list<GrantedAuthority>）

        //从数据库中根据用户名查询用户
        User userDB = userService.findByUsername(username);
        List<GrantedAuthority> authorityList = null;
        if (null != userDB) {
            //获得密码
            String password = userDB.getPassword();

            //创建权限集合
            authorityList = new ArrayList<>();

            //获得该用户的角色集合
            Set<Role> roles = userDB.getRoles();
            SimpleGrantedAuthority sga =null;
            for (Role role : roles) {
                sga = new SimpleGrantedAuthority(role.getKeyword());
                authorityList.add(sga);

                Set<Permission> permissions = role.getPermissions();
                if (null != permissions) {
                    for (Permission permission : permissions) {
                        sga = new SimpleGrantedAuthority(permission.getKeyword());
                        authorityList.add(sga);
                    }
                }
            }
            org.springframework.security.core.userdetails.User user = new org.springframework.security.core.userdetails.User(username,password,authorityList);
            return user;
        }
        return null;
    }
}
