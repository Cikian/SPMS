package com.spms.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Title: TimeUtils
 * @Author Cikian
 * @Package com.spms.utils
 * @Date 2024/4/8 13:43
 * @description: 时间工具类
 */
public class TimeUtils {
    public static String getNowTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    public static String getDate(String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }
}
