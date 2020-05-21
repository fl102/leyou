package com.leyou.service;

import com.leyou.domain.User;

public interface UserService {

    Boolean checkUser(String data, String type);

    void sendSms(String phone);

    void register(User user, String code);

    User findUser(String username, String password);
}
