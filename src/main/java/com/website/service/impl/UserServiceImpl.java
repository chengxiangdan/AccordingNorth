package com.website.service.impl;

import com.website.entity.UserInfo;
import com.website.mapper.UserMapper;
import com.website.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public long insertUser(UserInfo user) {
        long flag=userMapper.insertUser(user);
        return flag;
    }

    @Override
    public UserInfo findUserInfo(UserInfo user) {
        UserInfo userInfo=userMapper.findUserInfo(user);
        return userInfo;
    }
}
