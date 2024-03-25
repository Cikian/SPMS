package com.spms.filter;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.spms.common.Result;
import com.spms.common.ResultCode;
import com.spms.entity.LoginUser;
import com.spms.utils.JwtUtils;
import com.spms.utils.WebUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.spms.common.ResultCode.SUCCESS;
import static com.spms.common.ResultCode.UNAUTHORIZED;
import static com.spms.constants.RedisConstants.USER_LOGIN;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        获取token
        String token = request.getHeader("token");

//        如果token为空放行
        if (StringUtils.isEmpty(token)) {
            filterChain.doFilter(request, response);
            return;
        }

//        不为空解析token，获取userId，如果解析失败直接返回
        String userId = null;
        try {
            userId = JwtUtils.parseJWT(token);
        } catch (Exception e) {
            WebUtils.customResponse(response, JSONObject.toJSONString(Result.fail(UNAUTHORIZED.getCode(), "用户认证失败，请重新登录")));
            return;
        }

//        解析成功，从redis中获取用户信息
        LoginUser loginUser = JSONObject.parseObject(redisTemplate.opsForValue().get(USER_LOGIN + userId), LoginUser.class);
        if (ObjectUtils.isNull(loginUser)) {
            WebUtils.customResponse(response, JSONObject.toJSONString(Result.fail(UNAUTHORIZED.getCode(), "用户认证失败，请重新登录")));
            return;
        }

//        保存到SecurityContextHolder中，放行
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(loginUser, null, null));
        filterChain.doFilter(request, response);
    }
}
