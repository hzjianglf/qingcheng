package com.qingcheng.controller.system;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.PageResult;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.system.Admin;
import com.qingcheng.pojo.system.LoginLog;
import com.qingcheng.service.system.AdminService;
import com.qingcheng.service.system.LoginLogService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Reference
    public AdminService adminService;

    @Reference
    private LoginLogService loginLogService;

    @GetMapping("/findAll")
    public List<Admin> findAll(){
        return adminService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Admin> findPage(int page, int size){
        return adminService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Admin> findList(@RequestBody Map<String,Object> searchMap){
        return adminService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Admin> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  adminService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Admin findById(Integer id){
        return adminService.findById(id);
    }


    @PostMapping("/add")
    public Result add(@RequestBody Admin admin){
        adminService.add(admin);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Admin admin){
        adminService.update(admin);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Integer id){
        adminService.delete(id);
        return new Result();
    }

    @GetMapping("/showName")
    public Map showName(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, String> map = new HashMap<>();
        map.put("name",name);
        return map;
    }

    @GetMapping("/findPageByLogin")
    public PageResult<LoginLog> findPageByLogin(int page, int size){
        Map<String, Object> map = new HashMap<>();
        map.put("loginName",SecurityContextHolder.getContext().getAuthentication().getName());
        return loginLogService.findPage(map, page, size);
    }
}
