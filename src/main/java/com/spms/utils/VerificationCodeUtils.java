package com.spms.utils;

import org.springframework.stereotype.Component;

/**
 * @Title: VerificationCodeUtils
 * @Author Cikian
 * @Package com.spms.utils
 * @Date 2024/4/8 13:46
 * @description: 验证码工具类
 */

@Component
public class VerificationCodeUtils {
    /**
     * @param count 验证码位数
     * @return 验证码
     */
    public static String generatedCode(int count) {
        return String.valueOf((int) ((Math.random() * 9 + 1) * Math.pow(10, count - 1)));
    }

}
