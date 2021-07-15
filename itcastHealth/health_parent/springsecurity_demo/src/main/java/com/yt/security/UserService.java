package com.yt.security;

import com.yt.health.pojo.Permission;
import com.yt.health.pojo.Role;
import com.yt.health.pojo.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 包名：com.yt.security
 *
 * @author Yangtao
 * 日期：2021-07-06  20:25
 */
public class UserService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userInDB = findByUsername(username);
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();

        //遍历用户身上的角色
/*        Set<Role> roles = userInDB.getRoles();
        if (null != roles) {
            for (Role role : roles) {
                SimpleGrantedAuthority ga = new SimpleGrantedAuthority(role.getName());
                authorities.add(ga);
                //遍历每一个角色上的不同权限
                for (Permission permission : role.getPermissions()) {
                    ga = new SimpleGrantedAuthority(permission.getName());
                    authorities.add(ga);
                }
            }
        }*/

        //测试其它方式的认证
        SimpleGrantedAuthority ga = new SimpleGrantedAuthority("ROLE_ADMIN");
        authorities.add(ga);
        ga = new SimpleGrantedAuthority("add");
        //ga = new SimpleGrantedAuthority("ROLE_ABC");
        authorities.add(ga);

        // 登陆用户的认证信息,名称、密码、权限集合
        org.springframework.security.core.userdetails.User user = new org.springframework.security.core.userdetails.User(username,
                userInDB.getPassword(),authorities);
        return user;
    }

    private User findByUsername(String username) {
        if("admin".equals(username)) {
            User user = new User();
            user.setUsername("admin");
            //user.setPassword("admin");
            user.setPassword("$2a$10$5iv.tiXMsH.UUCZxbm8F2Oy4x/OLFzdJhzBKcZU.RfIcpmOZidm5a");
            Role role = new Role();
            role.setName("ROLE_ADMIN");
            Permission permission = new Permission();
            permission.setName("ADD_CHECKITEM");
            role.getPermissions().add(permission);

            Set<Role> roleList = new HashSet<Role>();
            roleList.add(role);

            user.setRoles(roleList);
            return user;
        }
        return null;
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder bpd = new BCryptPasswordEncoder();
        System.out.println(bpd.encode("yangtao"));
        System.out.println(bpd.encode("yangtao"));

        System.out.println(bpd.matches("yangtao", "$2a$10$vGztDiSkibFm11W4Tc1Gj.EMcnqKxrbokJ5I7AeHnLZtk61F60bKW"));
    }
}
