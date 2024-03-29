package com.spms.enums;

public enum ResultCode {

    SUCCESS(200), // 200: 成功
    FAIL(400), // 400: 失败
    UNAUTHORIZED(401), // 401: 未认证
    FORBIDDEN(403), // 403: 无权限
    NOT_FOUND(404), // 404: 未找到
    INTERNAL_SERVER_ERROR(500); // 500: 服务器内部错误

    private Integer code;

    ResultCode(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
