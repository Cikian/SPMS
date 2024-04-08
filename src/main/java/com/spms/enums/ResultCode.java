package com.spms.enums;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS(200), // 200: 成功
    FAIL(400), // 400: 失败

    UNAUTHORIZED(401), // 401: 未认证
    ACCOUNT_LOCKED(402),// 402: 账号被锁定
    FORBIDDEN(403), // 403: 无权限
    NOT_FOUND(404), // 404: 未找到

    INTERNAL_SERVER_ERROR(500); // 500: 服务器内部错误

    private Integer code;

    ResultCode(Integer code) {
        this.code = code;
    }

}
