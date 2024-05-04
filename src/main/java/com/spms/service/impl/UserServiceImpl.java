package com.spms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.*;
import com.spms.entity.RatedTimeCost;
import com.spms.entity.Role;
import com.spms.entity.RoleUser;
import com.spms.enums.ErrorCode;
import com.spms.enums.ResultCode;
import com.spms.mapper.RatedTimeCostMapper;
import com.spms.mapper.RoleMapper;
import com.spms.mapper.RoleUserMapper;
import com.spms.mapper.UserMapper;
import com.spms.security.LoginUser;
import com.spms.entity.User;
import com.spms.service.RatedTimeCostService;
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

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.spms.constants.RedisConstants.*;
import static com.spms.constants.SystemConstants.*;
import static com.spms.enums.ResourceType.EMPLOYEE;
import static com.spms.utils.RegexUtils.nickNameCheck;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;

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

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RatedTimeCostMapper ratedTimeCostMapper;

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

        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(loginUser.getUser(), userDTO, "userId", "email", "phoneNumber", "status", "gender", "createTime");
        map.put("userInfo", JSONObject.toJSONString(userDTO));
        map.put("hasRole", JSONObject.toJSONString(loginUser.getAuthorities()));
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
        user.setIsFirstLogin(true);
        boolean isSuccess = this.save(user);

        if (!isSuccess) {
            return Result.fail(ResultCode.FAIL.getCode(), "新增失败");
        }

        // 新增用户时，自动分配只读成员角色
        LambdaQueryWrapper<Role> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleLambdaQueryWrapper.eq(Role::getRoleName, "ROLE_read_only");
        Role role = roleMapper.selectOne(roleLambdaQueryWrapper);

        RoleUser roleUser = new RoleUser();
        roleUser.setUserId(user.getUserId());
        roleUser.setRoleId(role.getRoleId());
        roleUser.setDelFlag(NOT_DELETE);

        if (roleUserMapper.insert(roleUser) <= 0) {
            return Result.fail(ResultCode.FAIL.getCode(), "新增失败");
        }

        RatedTimeCost ratedTimeCost = new RatedTimeCost();
        ratedTimeCost.setResourceId(user.getUserId());
        ratedTimeCost.setResourceType(EMPLOYEE.getCode());
        ratedTimeCost.setDailyCost(BigDecimal.valueOf(0));
        ratedTimeCost.setMonthlyCost(BigDecimal.valueOf(0));
        ratedTimeCost.setDelFlag(NOT_DELETE);

        if (ratedTimeCostMapper.insert(ratedTimeCost) <= 0) {
            return Result.fail(ResultCode.FAIL.getCode(), "新增失败");
        }

        sendMailMessageService.sendEmail(javaMailSender, email, "SPMS账号密码", "【SPMS】您的用户名为：" + userName + "，初始密码为：" + password);
        return Result.success("新增成功，用户名为" + user.getUserName());
    }

    @Override
    public Result sendEmailCode(String email) {
        if (!RegexUtils.mailCheck(email)) {
            return Result.fail(ResultCode.FAIL.getCode(), "请检查邮箱格式是否正确");
        }

        Boolean sent = redisTemplate.hasKey(EMAIL_CODE + email);
        if (Boolean.TRUE.equals(sent)) {
            return Result.fail(ResultCode.FAIL.getCode(), "验证码已发送，请勿重复发送");
        }

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getEmail, email);
        if (this.count(userLambdaQueryWrapper) == 0) {
            return Result.fail(ResultCode.FAIL.getCode(), "邮箱尚未绑定账号");
        }

        String code = RandomStringGenerator.generateString(6);
        sendMailMessageService.sendEmail(javaMailSender, email, "SPMS验证码", "【SPMS】验证码为：" + code + "，5分钟内有效，请勿泄露和转发，如非本人操作，请忽略此邮件。");

        redisTemplate.opsForValue().set(EMAIL_CODE + email, code, EMAIL_CODE_TTL, TimeUnit.MINUTES);
        return Result.success("验证码发送成功，请注意查收");
    }

    @Override
    public Result verifyEmail(EmailVerifyDTO emailVerifyDTO) {
        if (StrUtil.isEmpty(emailVerifyDTO.getEmail()) || StrUtil.isEmpty(emailVerifyDTO.getCode())) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        boolean mailCheck = RegexUtils.mailCheck(emailVerifyDTO.getEmail());
        if (!mailCheck) {
            return Result.fail(ResultCode.FAIL.getCode(), "请检查邮箱格式是否正确");
        }

        String code = redisTemplate.opsForValue().get(EMAIL_CODE + emailVerifyDTO.getEmail());
        if (StrUtil.isEmpty(code)) {
            return Result.fail(ResultCode.FAIL.getCode(), "无效验证码");
        }
        boolean isSuccess = StrUtil.equals(code, emailVerifyDTO.getCode());
        return isSuccess ? Result.success("验证通过") : Result.fail(ResultCode.FAIL.getCode(), "验证码错误");
    }

    @Override
    @Transactional
    public Result updatePassword(PasswordUpdateDTO passwordUpdateDTO) {
        if (StrUtil.isEmpty(passwordUpdateDTO.getOldPassword()) || StrUtil.isEmpty(passwordUpdateDTO.getNewPassword())) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        if (!StrUtil.equals(passwordUpdateDTO.getNewPassword(), passwordUpdateDTO.getConfirmPassword())) {
            return Result.fail(ResultCode.FAIL.getCode(), "两次密码输入不一致");
        }

        boolean passwordCheck = RegexUtils.passwordCheck(passwordUpdateDTO.getNewPassword());
        if (!passwordCheck) {
            return Result.fail(ResultCode.FAIL.getCode(), "密码格式错误");
        }

        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = loginUser.getUser();

        boolean matches = bCryptPasswordEncoder.matches(passwordUpdateDTO.getOldPassword(), user.getPassword());
        if (!matches) {
            return Result.fail(ResultCode.FAIL.getCode(), "原密码错误");
        }

        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.eq(User::getUserId, user.getUserId())
                .set(User::getPassword, bCryptPasswordEncoder.encode(passwordUpdateDTO.getNewPassword()));
        boolean isSuccess = this.update(userLambdaUpdateWrapper);

        if (!isSuccess) {
            return Result.fail(ResultCode.FAIL.getCode(), "修改失败");
        }

        if (user.getIsFirstLogin()) {
            LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(User::getUserId, loginUser.getUser().getUserId())
                    .set(User::getIsFirstLogin, false);
            this.update(lambdaUpdateWrapper);
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

        //TODO:删除关联的成本信息

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
        if (userDTO == null || userDTO.getUserId() == null || userDTO.getStatus() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }
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
    public Result queryById(Long id) {
        if (id == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        User queryUser = this.getById(id);
        if (queryUser == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "用户不存在");
        }

        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(queryUser, userDTO);

        return Result.success(userDTO);
    }

    @Override
    public Result updateUserBaseInfo(User user) {
        if (user == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = loginUser.getUser();

        if (Objects.equals(user.getNickName(), currentUser.getNickName()) && Objects.equals(user.getGender(), currentUser.getGender())) {
            return Result.success("信息未发生修改");
        }

        if (!nickNameCheck(user.getNickName())) {
            return Result.fail(ResultCode.FAIL.getCode(), "昵称格式错误，请重新输入");
        }

        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.eq(User::getUserId, currentUser.getUserId())
                .set(User::getNickName, user.getNickName())
                .set(User::getGender, user.getGender());
        this.update(userLambdaUpdateWrapper);

        currentUser.setGender(user.getGender());
        currentUser.setNickName(user.getNickName());

        redisTemplate.opsForValue().set(USER_LOGIN + currentUser.getUserId(), JSONObject.toJSONString(loginUser), USER_LOGIN_TTL, TimeUnit.MINUTES);
        return Result.success("修改信息成功");
    }

    @Override
    public Result queryCurrentUser() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = loginUser.getUser();

        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO, "userId", "status", "createTime");

        return Result.success(userDTO);
    }

    @Override
    public Result queryCanAddToProject() {
        List<CreateProjectAddUserDTO> createProjectAddUserDTOS = userMapper.queryCanAddToProject();
        System.out.println(createProjectAddUserDTOS);
        // 将相同Id的用户的position合并，加顿号

        Map<Long, String> positionMap = new HashMap<>();
        for (CreateProjectAddUserDTO createProjectAddUserDTO : createProjectAddUserDTOS) {
            if (positionMap.containsKey(createProjectAddUserDTO.getUserId())) {
                String s = positionMap.get(createProjectAddUserDTO.getUserId());
                s = s + "、" + createProjectAddUserDTO.getPosition();
                positionMap.put(createProjectAddUserDTO.getUserId(), s);
            } else {
                positionMap.put(createProjectAddUserDTO.getUserId(), createProjectAddUserDTO.getPosition());
            }
        }

        System.out.println(positionMap);

        List<CreateProjectAddUserDTO> resultList = new ArrayList<>();
        for (Map.Entry<Long, String> entry : positionMap.entrySet()) {
            CreateProjectAddUserDTO createProjectAddUserDTO = new CreateProjectAddUserDTO();
            Long userId = entry.getKey();
            createProjectAddUserDTO.setUserId(userId);
            createProjectAddUserDTO.setPosition(entry.getValue());
            for (CreateProjectAddUserDTO dto : createProjectAddUserDTOS){
                if (userId.equals(dto.getUserId())) {
                    createProjectAddUserDTO.setUserName(dto.getUserName());
                    createProjectAddUserDTO.setAvatar(dto.getAvatar());
                }
            }
            resultList.add(createProjectAddUserDTO);
        }

        Integer code = createProjectAddUserDTOS.isEmpty() ? ErrorCode.GET_FAIL : ErrorCode.GET_SUCCESS;
        String msg = createProjectAddUserDTOS.isEmpty() ? "获取失败" : "获取成功";
        return new Result(code, msg, resultList);
    }

}
