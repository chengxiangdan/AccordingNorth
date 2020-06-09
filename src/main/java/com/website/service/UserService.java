package com.website.service;


import com.website.entity.UserInfo;

public interface UserService {
    long insertUser(UserInfo user);

    UserInfo findUserInfo(UserInfo userInfo);
}
