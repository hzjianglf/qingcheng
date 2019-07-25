package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.system.LoginLog;
import com.qingcheng.service.system.LoginLogService;
import com.qingcheng.utils.WebUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @Auther: wanjunyi
 * @Date: 2019/7/6 21:01
 * @Description:
 */
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Reference
    LoginLogService loginLogService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletRequest.getRequestDispatcher("/main.html").forward(httpServletRequest,httpServletResponse);
        LoginLog loginLog = new LoginLog();
        String ip = httpServletRequest.getRemoteAddr();
        loginLog.setLoginName(authentication.getName());
        loginLog.setIp(ip);
        loginLog.setLoginTime(new Date());
        loginLog.setBrowserName(WebUtil.getBrowserName(httpServletRequest.getHeader("user-agent")));
        loginLog.setLocation(WebUtil.getCityByIP(ip));
        loginLogService.add(loginLog);
    }
}
