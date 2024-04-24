package com.spms.constants;

public class RegexPatterns {
    public static final String NICK_NAME_REGEX = "^[\\u4E00-\\u9FA5\\w]{3,18}$";
    public static final String GENDER_REGEX = "^[FMN]$";
    public static final String PHONE_REGEX = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    public static final String PASSWORD_REGEX = "^[a-zA-Z0-9]{6,32}$";
    public static final String USERNAME_REGEX = "^[\\u4e00-\\u9fa5a-zA-Z0-9]{3,16}$";
}
