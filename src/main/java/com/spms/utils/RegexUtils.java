package com.spms.utils;

import cn.hutool.core.util.StrUtil;

import static com.spms.constants.RegexPatterns.*;

public class RegexUtils {

    public static boolean nickNameCheck(String nickName) {
        return mismatch(nickName, NICK_NAME_REGEX);
    }

    public static boolean passwordCheck(String password) {
        return mismatch(password, PASSWORD_REGEX);
    }

    public static boolean genderCheck(String gender) {
        return mismatch(gender, GENDER_REGEX);
    }

    public static boolean mailCheck(String email) {
        return mismatch(email, EMAIL_REGEX);
    }

    // 校验是否符合正则格式，符合返回true
    private static boolean mismatch(String str, String regex) {
        if (StrUtil.isBlank(str)) {
            return false;
        }
        return str.matches(regex);
    }
}
