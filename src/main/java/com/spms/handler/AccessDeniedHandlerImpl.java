package com.spms.handler;

import com.alibaba.fastjson.JSONObject;

import com.spms.dto.Result;
import com.spms.utils.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.spms.enums.ResultCode.FORBIDDEN;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        WebUtils.customResponse(response, JSONObject.toJSONString(Result.fail(FORBIDDEN.getCode(), "无权访问")));
    }
}
