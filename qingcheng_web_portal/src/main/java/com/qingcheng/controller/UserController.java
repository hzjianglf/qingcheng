package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.user.User;
import com.qingcheng.service.user.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @Auther: wanjunyi
 * @Date: 2019/7/17 14:47
 * @Description:
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @GetMapping("/sendSms")
    public Result sendSms(String phone) {
        if (phone==null||"".equals(phone)){
            throw new RuntimeException("手机号不能为空");
        }
        userService.sendSms(phone);
        return new Result();
    }

    @PostMapping("/save")
    public Result save(@RequestBody User user, String smsCode) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encode);
        userService.save(user, smsCode);
        return new Result();
    }
}
