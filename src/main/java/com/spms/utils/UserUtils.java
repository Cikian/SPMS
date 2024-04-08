package com.spms.utils;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * @Title: UserUtils
 * @Author Cikian
 * @Package com.spms.utils
 * @Date 2024/4/8 13:45
 * @description: 用户相关工具类
 */
public class UserUtils {
    // 校验密码
    public static boolean checkPassword(String uPassword, String dbPassword) {
        uPassword += "cikian";
        uPassword = DigestUtils.md5DigestAsHex(uPassword.getBytes(StandardCharsets.UTF_8));
        return uPassword.equals(dbPassword);
    }
}
