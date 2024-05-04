package com.spms.filter;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.spms.dto.Result;
import com.spms.security.LoginUser;
import com.spms.utils.JwtUtils;
import com.spms.utils.WebUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.spms.constants.RedisConstants.USER_LOGIN_TTL;
import static com.spms.enums.ResultCode.UNAUTHORIZED;
import static com.spms.constants.RedisConstants.USER_LOGIN;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        获取token
        String token = request.getHeader("token");
        System.out.println("获取header中的token："  + token);
//        如果token为空放行
        if (StringUtils.isEmpty(token)) {
            System.out.println("token为空");
            filterChain.doFilter(request, response);
            return;
        }

//        不为空解析token，获取userId，如果解析失败直接返回
        String userId = null;
        try {
            userId = JwtUtils.parseJWT(token);
            //在token过期前15分钟，重新生成token
            if (JwtUtils.getRemainingTime(token) - 15 * 60 * 1000 < 0) {
                redisTemplate.expire(USER_LOGIN + userId, USER_LOGIN_TTL, TimeUnit.MINUTES);
                String newToken = JwtUtils.createJWT(userId);
                response.setHeader("newToken", newToken);
            }
        } catch (Exception e) {
            System.out.println("token过期，抛异常");
            WebUtils.customResponse(response, JSONObject.toJSONString(Result.fail(UNAUTHORIZED.getCode(), "用户认证失败，请重新登录！")));
            return;
        }

//        解析成功，从redis中获取用户信息
        LoginUser loginUser = JSONObject.parseObject(redisTemplate.opsForValue().get(USER_LOGIN + userId), LoginUser.class);
        if (ObjectUtils.isNull(loginUser)) {
            WebUtils.customResponse(response, JSONObject.toJSONString(Result.fail(UNAUTHORIZED.getCode(), "用户认证失败，请重新登录！")));
            return;
        }

//        保存到SecurityContextHolder中，放行
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities()));
        filterChain.doFilter(request, response);
    }
}
