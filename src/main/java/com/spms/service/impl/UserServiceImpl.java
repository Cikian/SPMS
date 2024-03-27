package com.spms.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.Result;
import com.spms.enums.ResultCode;
import com.spms.mapper.UserMapper;
import com.spms.security.LoginUser;
import com.spms.entity.User;
import com.spms.service.UserService;
import com.spms.utils.GenerateRandomUsernameUtils;
import com.spms.utils.JwtUtils;
import com.spms.utils.RegexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.spms.constants.RedisConstants.USER_LOGIN;
import static com.spms.constants.SystemConstants.DEFAULT_AVATAR_URL;
import static com.spms.constants.SystemConstants.DEFAULT_PASSWORD;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService{

    @Autowired
    private AuthenticationManager authentication;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;

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

    @Override
    public Result logout() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boolean isDelete = redisTemplate.delete(USER_LOGIN + loginUser.getUser().getUserId());
        return Boolean.TRUE.equals(isDelete) ? Result.success("退出成功") : Result.fail(ResultCode.FAIL.getCode(), "退出失败");
    }

    @Override
    public Result add(User user) {
        String nickName = user.getNickName();
        String gender = user.getGender();

        if (!RegexUtils.isNickNameValid(nickName)) {
            return Result.fail(ResultCode.FAIL.getCode(), "请按照格式输入昵称！");
        }

        if (!RegexUtils.isGenderValid(gender)) {
            return Result.fail(ResultCode.FAIL.getCode(), "非法输入！");
        }

        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        user.setUserName(GenerateRandomUsernameUtils.generateRandomUsername());
        user.setPassword(bCryptPasswordEncoder.encode(DEFAULT_PASSWORD));
        user.setNickName(nickName);
        user.setGender(gender);
        user.setAvatar(DEFAULT_AVATAR_URL);
        user.setCreateBy(loginUser.getUser().getUserId());
        user.setUpdateBy(loginUser.getUser().getUserId());

        boolean isSuccess = this.save(user);
        return isSuccess ? Result.success("新增成功！") : Result.fail(ResultCode.FAIL.getCode(), "新增失败！");
    }
}
