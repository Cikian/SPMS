package com.spms.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.spms.common.Result;
import com.spms.entity.LoginUser;
import com.spms.entity.User;
import com.spms.mapper.UserMapper;
import com.spms.service.UserService;
import com.spms.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.spms.constants.RedisConstants.USER_LOGIN;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private AuthenticationManager authentication;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Result login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
        Authentication authenticate = authentication.authenticate(authenticationToken);

        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("登录失败");
        }

        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getUserId().toString();
        String jwt = JwtUtils.createJWT(userId);

        redisTemplate.opsForValue().set(USER_LOGIN + userId, JSONObject.toJSONString(loginUser));

        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);
        return Result.success("登录成功！", map);
    }
}
