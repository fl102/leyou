package com.leyou.service.impl;

import com.leyou.dao.UserDao;
import com.leyou.domain.User;
import com.leyou.service.UserService;
import com.leyou.utils.CodecUtils;
import com.leyou.utils.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    static final String KEY_PREFIX = "user:verify:";

    @Override
    public Boolean checkUser(String data, String type) {
        User user = new User();
        if (StringUtils.equals(type, "1")){
            user.setUsername(data);
        } else if (StringUtils.equals(type, "2")) {
            user.setPassword(data);
        } else {
            return null;
        }
        return userDao.selectCount(user) == 0;
    }

    @Override
    public void sendSms(String phone) {
        if (StringUtils.isBlank(phone)){
            return;
        }
        //生成随机数
        String code = NumberUtils.generateCode(6);
        //发送消息
        Map<String, String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);
        amqpTemplate.convertAndSend("LEYOU.SMS.EXCHANGE", "sms.verify.code", msg);
        //保存到redis
        redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
    }

    @Override
    @Transactional
    public void register(User user, String code) {
        //校验短信验证码
        String redisCode = this.redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if (!StringUtils.equals(redisCode, code)){
            return;
        }
        //生成盐
        String salt = CodecUtils.generateSalt();
        //生成盐和密码
        user.setSalt(salt);
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));
        user.setId(null);
        user.setCreated(new Date());

        //保存用户
        this.userDao.insertSelective(user);
        try {
            this.redisTemplate.delete(KEY_PREFIX + user.getPhone());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public User findUser(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return null;
        }
        //根据用户名查询用户
        User record = new User();
        record.setUsername(username);
        User findUser = this.userDao.selectOne(record);

        //判断用户
        if(findUser == null){
            return null;
        }
        //对密码加盐加密
         password = CodecUtils.md5Hex(password,findUser.getSalt());
        //判断密码和数据库中的密码
        if (StringUtils.equals(findUser.getPassword(), password)){
            return findUser;
        }
        return null;
    }
}
