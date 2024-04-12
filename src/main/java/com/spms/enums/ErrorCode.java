package com.spms.enums;

public class ErrorCode {
    /**
     * &#064;CODE说明：操作编号+结果编号
     * <br>操作编号：
     * 1. 20：查询操作
     * 2. 30：新增操作
     * 3. 40：修改操作
     * 4. 50：删除操作
     * 5. 60: 登录
     * 5. 10: Common
     *
     * <br>@结果编号：
     * 1. 01：成功
     * 2. 02：失败
     * 3. 99：未知错误
     * 4. 00：系统错误
     * 5. 03：业务错误
     * 6. 04：参数错误
     * 7. 05：权限错误
     */
    public static final Integer COMMON_SUCCESS = 1001;
    public static final Integer COMMON_FAIL = 1002;
    public static final Integer ADD_SUCCESS = 3001;
    public static final Integer ADD_FAIL = 3002;
    public static final Integer UPDATE_SUCCESS = 4001;
    public static final Integer UPDATE_FAIL = 4002;
    public static final Integer DELETE_SUCCESS = 5001;
    public static final Integer DELETE_FAIL = 5002;
    public static final Integer GET_SUCCESS = 2001;
    public static final Integer GET_FAIL = 2002;
    public static final Integer LOGIN_SUCCESS = 6001;
    public static final Integer LOGIN_FAIL = 6002;
    public static final Integer SYSTEM_ERR = 199;
    public static final Integer BUSINESS_ERR = 299;
    public static final Integer UNKNOW_ERR = 9999;


}
