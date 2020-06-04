package com.website.mapper;


import com.website.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface UserMapper {
    //添加数据
    long insertUser(UserInfo user);
    //查询数据
//   / List<UserInfo> getUser(UserInfo user);
}
