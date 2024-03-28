package com.spms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.EmailVerifyDTO;
import com.spms.dto.PasswordUpdateDTO;
import com.spms.dto.Result;
import com.spms.enums.ResultCode;
import com.spms.mapper.UserMapper;
import com.spms.security.LoginUser;
import com.spms.entity.User;
import com.spms.service.UserService;
import com.spms.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.spms.constants.RedisConstants.*;
import static com.spms.constants.SystemConstants.DEFAULT_AVATAR_URL;
import static com.spms.constants.SystemConstants.DEFAULT_PASSWORD;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private AuthenticationManager authentication;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JavaMailSender javaMailSender;

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
        return Result.success("登录成功", map);
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
            return Result.fail(ResultCode.FAIL.getCode(), "请按照格式输入昵称");
        }

        if (!RegexUtils.isGenderValid(gender)) {
            return Result.fail(ResultCode.FAIL.getCode(), "非法输入");
        }

        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        user.setUserName(RandomUsernameGenerator.generateRandomUsername());
        user.setPassword(bCryptPasswordEncoder.encode(DEFAULT_PASSWORD));
        user.setNickName(nickName);
        user.setGender(gender);
        user.setAvatar(DEFAULT_AVATAR_URL);
        user.setCreateBy(loginUser.getUser().getUserId());
        user.setUpdateBy(loginUser.getUser().getUserId());

        boolean isSuccess = this.save(user);
        return isSuccess ? Result.success("新增成功") : Result.fail(ResultCode.FAIL.getCode(), "新增失败");
    }

    @Override
    public Result sendEmailCode(String email) {
        if (!RegexUtils.isMailValid(email)) {
            return Result.fail(ResultCode.FAIL.getCode(), "请检查邮箱格式是否正确");
        }

        String code = VerificationCodeGenerator.generateCode(6);
        SendMailMessageUtils.sendEmail(javaMailSender, email, code);

        redisTemplate.opsForValue().set(EMAIL_CODE + email, code, EMAIL_CODE_TTL, TimeUnit.MINUTES);
        return Result.success("发送成功");
    }

    @Override
    public Result verifyEmail(EmailVerifyDTO emailVerifyDTO) {
        String code = redisTemplate.opsForValue().get(EMAIL_CODE + emailVerifyDTO.getEmail());
        if (StrUtil.isEmpty(code)) {
            return Result.fail(ResultCode.FAIL.getCode(), "无效验证码");
        }
        boolean isSuccess = StrUtil.equals(code, emailVerifyDTO.getCode());
        return isSuccess ? Result.success("验证通过") : Result.fail(ResultCode.FAIL.getCode(), "验证码错误");
    }

    @Override
    public Result updatePassword(PasswordUpdateDTO passwordUpdateDTO) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = loginUser.getUser();

        boolean matches = bCryptPasswordEncoder.matches(passwordUpdateDTO.getOldPassword(), user.getPassword());
        if (!matches) {
            return Result.fail(ResultCode.FAIL.getCode(), "原密码错误");
        }

        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.set(User::getPassword, bCryptPasswordEncoder.encode(passwordUpdateDTO.getNewPassword()));
        boolean isSuccess = this.update(userLambdaUpdateWrapper);

        if (!isSuccess) {
            return Result.fail(ResultCode.FAIL.getCode(), "修改失败");
        }

        redisTemplate.delete(USER_LOGIN + user.getUserId());
        return Result.success("修改成功");
    }

}
