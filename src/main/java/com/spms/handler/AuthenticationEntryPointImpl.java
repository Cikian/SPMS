package com.spms.handler;

import com.alibaba.fastjson.JSONObject;

import com.spms.dto.Result;
import com.spms.enums.ResultCode;
import com.spms.security.LoginUser;
import com.spms.utils.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.spms.constants.RedisConstants.USER_LOGIN;
import static com.spms.enums.ResultCode.UNAUTHORIZED;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        System.out.println(authException);

        String exceptionName = authException.toString();
        if (exceptionName.contains("Locked")) {
            WebUtils.customResponse(response, JSONObject.toJSONString(Result.fail(ResultCode.ACCOUNT_LOCKED.getCode(), "用户已被锁定，请联系管理员！")));
        } else if (exceptionName.contains("BadCredentials")) {
            WebUtils.customResponse(response, JSONObject.toJSONString(Result.fail(ResultCode.FAIL.getCode(), "用户名或密码错误！")));
        } else if (exceptionName.contains("Disabled")) {
            WebUtils.customResponse(response, JSONObject.toJSONString(Result.fail(ResultCode.ACCOUNT_DISABLED.getCode(), "用户已被禁用，请联系管理员！")));
        } else {
            LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = loginUser.getUser().getUserId();
            redisTemplate.delete(USER_LOGIN + userId);
            WebUtils.customResponse(response, JSONObject.toJSONString(Result.fail(UNAUTHORIZED.getCode(), "用户认证失败，请重新登录！")));
        }
    }
}
