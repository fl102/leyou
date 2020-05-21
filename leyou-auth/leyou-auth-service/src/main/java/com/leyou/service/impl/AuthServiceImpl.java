package com.leyou.service.impl;

import com.leyou.client.UserClient;
import com.leyou.config.JwtProperties;
import com.leyou.domain.User;
import com.leyou.domain.UserInfo;
import com.leyou.service.AuthService;
import com.leyou.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtProperties jwtProperties;
    @Override
    public String accredit(String username,String password) {
        //校验用户名和密码
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        User resultUser = this.userClient.findUser(username, password);
        //判断用户
        if (resultUser == null){
            return null;
        }
        try {
            UserInfo userInfo = new UserInfo();
            userInfo.setId(resultUser.getId());
            userInfo.setUsername(resultUser.getUsername());
            return JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //生成token
        return null;
    }
}
