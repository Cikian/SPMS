package com.spms.handler;

import com.alibaba.fastjson.JSONObject;

import com.spms.dto.Result;
import com.spms.utils.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.spms.enums.ResultCode.UNAUTHORIZED;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        WebUtils.customResponse(response, JSONObject.toJSONString(Result.fail(UNAUTHORIZED.getCode(), "用户认证失败，请重新登录！")));
    }
}
