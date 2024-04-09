package com.spms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.EmailVerifyDTO;
import com.spms.dto.PasswordUpdateDTO;
import com.spms.dto.Result;
import com.spms.dto.UserDTO;
import com.spms.entity.Role;
import com.spms.entity.RoleUser;
import com.spms.enums.ResultCode;
import com.spms.mapper.RoleUserMapper;
import com.spms.mapper.UserMapper;
import com.spms.security.LoginUser;
import com.spms.entity.User;
import com.spms.service.RoleUserService;
import com.spms.service.UserService;
import com.spms.utils.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.spms.constants.RedisConstants.*;
import static com.spms.constants.SystemConstants.*;

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
    private SendMailMessageService sendMailMessageService;

    @Autowired
    private RoleUserMapper roleUserMapper;

    @Override
    public Result login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
        Authentication authenticate = authentication.authenticate(authenticationToken);

        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("登录失败");
        }

        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getUserId().toString();

        if (Boolean.TRUE.equals(redisTemplate.hasKey(USER_LOGIN + userId))) {
            return Result.fail(ResultCode.FAIL.getCode(), "该账号已在其他地方登录");
        }

        String jwt = JwtUtils.createJWT(userId);
        redisTemplate.opsForValue().set(USER_LOGIN + userId, JSONObject.toJSONString(loginUser), USER_LOGIN_TTL, TimeUnit.MINUTES);

        Boolean isFirstLogin = loginUser.getUser().getIsFirstLogin();
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);
        if (Boolean.TRUE.equals(isFirstLogin)) {
            map.put("isFirstLogin", "true");
        }
        return Result.success("登录成功", map);
    }

    @Override
    public Result logout() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boolean isDelete = redisTemplate.delete(USER_LOGIN + loginUser.getUser().getUserId());
        return Boolean.TRUE.equals(isDelete) ? Result.success("退出成功") : Result.fail(ResultCode.FAIL.getCode(), "退出失败");
    }

    @Override
    @Transactional
    public Result add(User user) {
        String email = user.getEmail();
        if (!RegexUtils.mailCheck(email)) {
            return Result.fail(ResultCode.FAIL.getCode(), "请检查邮箱格式是否正确");
        }

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getEmail, email);
        if (this.count(userLambdaQueryWrapper) > 0) {
            return Result.fail(ResultCode.FAIL.getCode(), "邮箱已存在");
        }

        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String password = RandomStringGenerator.generateNumber(8);
        String userName = RandomUsernameGenerator.generateRandomUsername();
        user.setUserName(userName);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setNickName(RandomStringGenerator.generateString(10));
        user.setGender(DEFAULT_GENDER);
        user.setAvatar(DEFAULT_AVATAR_URL);
        user.setCreateBy(loginUser.getUser().getUserId());
        user.setUpdateBy(loginUser.getUser().getUserId());

        boolean isSuccess = this.save(user);

        if (!isSuccess) {
            return Result.fail(ResultCode.FAIL.getCode(), "新增失败");
        }

        sendMailMessageService.sendEmail(javaMailSender, email, "SPMS账号密码", "【SPMS】您的用户名为：" + userName + "，初始密码为：" + password);

        return Result.success("新增成功");
    }

    @Override
    public Result sendEmailCode(String email) {
        if (!RegexUtils.mailCheck(email)) {
            return Result.fail(ResultCode.FAIL.getCode(), "请检查邮箱格式是否正确");
        }

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getEmail, email);
        if (this.count(userLambdaQueryWrapper) == 0) {
            return Result.fail(ResultCode.FAIL.getCode(), "邮箱尚未绑定账号");
        }

        String code = RandomStringGenerator.generateString(6);
        sendMailMessageService.sendEmail(javaMailSender, email, "SPMS验证码", "【SPMS】验证码为：" + code + "，5分钟内有效，请勿泄露和转发，如非本人操作，请忽略此短信。");

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

    @Override
    @Transactional
    public Result delete(Long[] ids) {
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.set(User::getDelFlag, DELETE).in(User::getUserId, ids);
        this.update(userLambdaUpdateWrapper);

        // 删除用户时，删除用户与角色的关联
        LambdaUpdateWrapper<RoleUser> roleUserLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        roleUserLambdaUpdateWrapper.set(RoleUser::getDelFlag, DELETE).in(RoleUser::getUserId, ids);
        roleUserMapper.update(roleUserLambdaUpdateWrapper);
        return Result.success("删除成功");
    }

    @Override
    public Result list(UserDTO userDTO, Integer page, Integer size) {
        Page<User> userPage = new Page<>(page, size);
        Page<UserDTO> userDTOPage = new Page<>();

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper
                .like(!Objects.isNull(userDTO.getNickName()), User::getNickName, userDTO.getNickName())
                .eq(!Objects.isNull(userDTO.getUserName()), User::getUserName, userDTO.getUserName())
                .eq(!Objects.isNull(userDTO.getGender()), User::getGender, userDTO.getGender())
                .eq(!Objects.isNull(userDTO.getEmail()), User::getEmail, userDTO.getEmail())
                .eq(!Objects.isNull(userDTO.getStatus()), User::getStatus, userDTO.getStatus())
                .eq(!Objects.isNull(userDTO.getPhoneNumber()), User::getPhoneNumber, userDTO.getPhoneNumber())
                .eq(User::getDelFlag, NOT_DELETE)
                .orderByAsc(User::getCreateTime);
        this.page(userPage, userLambdaQueryWrapper);

        if (userPage.getRecords().isEmpty()) {
            return Result.fail(ResultCode.FAIL.getCode(), "暂无数据");
        }

        BeanUtils.copyProperties(userPage, userDTOPage, "records");

        List<User> userList = userPage.getRecords();
        List<UserDTO> userDTOList = userList.stream().map(item -> {
            UserDTO userDTO1 = new UserDTO();
            BeanUtils.copyProperties(item, userDTO1);
            return userDTO1;
        }).toList();
        userDTOPage.setRecords(userDTOList);

        return Result.success(userDTOPage);
    }

    @Override
    @Transactional
    public Result updateStatus(UserDTO userDTO) {
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.eq(User::getUserId, userDTO.getUserId())
                .set(User::getStatus, userDTO.getStatus());
        boolean isUpdate = this.update(userLambdaUpdateWrapper);

        if (!isUpdate) {
            return Result.fail(ResultCode.FAIL.getCode(), "修改失败");
        }

        //如果redis中存在该用户的登录信息，则删除
        if (Boolean.TRUE.equals(redisTemplate.hasKey(USER_LOGIN + userDTO.getUserId()))) {
            redisTemplate.delete(USER_LOGIN + userDTO.getUserId());
        }
        return Result.success("修改成功");
    }

    @Override
    @Transactional
    public Result assignRole(Long userId, List<Long> roleIds) {
        if (userId == null || roleIds == null || roleIds.isEmpty()) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LambdaUpdateWrapper<RoleUser> deleteWrapper = new LambdaUpdateWrapper<>();
        deleteWrapper.eq(RoleUser::getUserId, userId)
                .set(RoleUser::getDelFlag, DELETE);
        roleUserMapper.update(deleteWrapper);

        for (Long roleId : roleIds) {
            LambdaQueryWrapper<RoleUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RoleUser::getUserId, userId)
                    .eq(RoleUser::getRoleId, roleId);
            RoleUser existingRoleUser = roleUserMapper.selectOne(queryWrapper);

            if (existingRoleUser != null) {
                // 如果存在相同的role_id和user_id的记录，且del_flag为true，则更新del_flag为false
                if (existingRoleUser.getDelFlag()) {
                    existingRoleUser.setDelFlag(NOT_DELETE);
                    LambdaUpdateWrapper<RoleUser> roleUserLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                    roleUserLambdaUpdateWrapper.eq(RoleUser::getUserId, userId)
                            .eq(RoleUser::getRoleId, roleId)
                            .set(RoleUser::getDelFlag, NOT_DELETE);
                    roleUserMapper.update(roleUserLambdaUpdateWrapper);
                }
            } else {
                // 如果不存在相同的role_id和user_id的记录，则插入新记录
                RoleUser roleUser = new RoleUser();
                roleUser.setUserId(userId);
                roleUser.setRoleId(roleId);
                roleUser.setDelFlag(NOT_DELETE);
                roleUserMapper.insert(roleUser);
            }
        }
        return Result.success("分配成功");
    }

}
