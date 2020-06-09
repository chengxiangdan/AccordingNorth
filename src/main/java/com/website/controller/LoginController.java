package com.website.controller;

import com.alibaba.fastjson.JSONObject;
import com.website.common.ResultDto;
import com.website.entity.UserInfo;
import com.website.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @ResponseBody
    @RequestMapping("/sys/login")
    public ResultDto login(String username,String password){
        HashMap<String, String> dataMap=new HashMap<String, String>();

        dataMap.put("status","200");
        dataMap.put("message","登录成功");
        String jsonObject=JSONObject.toJSONString(dataMap);
        System.out.println(jsonObject);
        return ResultDto.ok("20000");
    }

    @RequestMapping("/logins")
    public ResultDto<Object> logins(@RequestBody UserInfo userInfo){
        HashMap<String, String> dataMap=new HashMap<String, String>();
        if("".equals(userInfo.getUserName())||"".equals(userInfo.getPassWord())){
            dataMap.put("message","请输入密码");
        }
        UserInfo user=userService.findUserInfo(userInfo);
        if (user==null){
            dataMap.put("message","密码错误");
        }
        dataMap.put("user",JSONObject.toJSONString(user));
        dataMap.put("status","200");
        dataMap.put("message","登录成功");
        String jsonObject=JSONObject.toJSONString(dataMap);
        System.out.println(jsonObject);
        return new ResultDto<Object>(200,"登录成功",user);
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
