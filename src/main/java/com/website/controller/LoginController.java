package com.website.controller;

import com.website.entity.UserInfo;
import com.website.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping(value = "/login")
public class LoginController {
    @Autowired
    private UserService userService;

    @RequestMapping("/login")
    public String login(){
        String userName="";
        String passWord="";

        if("".equals(userName)||"".equals(passWord)){
            return "密码错误";
        }else {

        }
        return "loginssss`";
    }

    /**
     * 注册用户
     * @return
     */
    @RequestMapping("/registration")
    public String userRegistration(){
        String userName="admin";
        String passWord="admin";
        //姓名
        String name="admin";
        //手机号
        String phone="12345678901";
        //年龄
        int age=22;
        //性别
        int sex=0;
        //生日时间
        Date birthdayTime=new Date();
        //用户级别
        int userLevel=1;
        //爱好
        String hobby="篮球,乒乓球,跑步,电影,";
        UserInfo userInfo=new UserInfo();
        userInfo.setUserName(userName);
        userInfo.setPassWord(passWord);
        userInfo.setName(name);
        userInfo.setPhone(phone);
        userInfo.setAge(age);
        userInfo.setSex(sex);

        userInfo.setBirthdayTime(birthdayTime);
        userInfo.setUserLevel(userLevel);
        long l=userService.insertUser(userInfo);
        System.out.println(l);



        return "loginssss`";
    }
}
