package com.spms.utils;

import cn.hutool.core.util.StrUtil;

import static com.spms.constants.RegexPatterns.*;

public class RegexUtils {

    public static boolean isNickNameValid(String nickName) {
        return mismatch(nickName, NICK_NAME_REGEX);
    }

    public static boolean isPasswordValid(String password) {
        return mismatch(password, PASSWORD_REGEX);
    }

    public static boolean isGenderValid(String gender) {
        return mismatch(gender, GENDER_REGEX);
    }

    // 校验是否符合正则格式，符合返回true
    private static boolean mismatch(String str, String regex) {
        if (StrUtil.isBlank(str)) {
            return false;
        }
        return str.matches(regex);
    }
}
