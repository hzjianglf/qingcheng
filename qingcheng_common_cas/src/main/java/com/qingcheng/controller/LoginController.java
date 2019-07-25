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
 * @Date: 2019/7/22 08:58
 * @Description:
 */

@RestController
@RequestMapping("/login")
public class LoginController {

    @GetMapping("/username")
    public Map username(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(name)){
            name = "";
        }
        Map map = new HashMap();
        map.put("username", name);
        return map;
    }
}
