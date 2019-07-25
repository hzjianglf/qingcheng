package com.qingcheng.controller;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: wanjunyi
 * @Date: 2019/7/19 16:17
 * @Description:
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/username")
    public Map username(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(username)) {
            username = "";
        }
        System.out.println(username);
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        return map;
    }

}
