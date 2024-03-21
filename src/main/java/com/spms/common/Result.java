package com.spms.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Result {

    private ResultCode code;

    private String message;

    private Object data;

    private Result() {}

    public static Result success() {
        return new Result(ResultCode.SUCCESS, null, null);
    }

    public static Result success(String msg, Object data) {
        return new Result(ResultCode.SUCCESS, msg, data);
    }

    public static Result success(Object data) {
        return new Result(ResultCode.SUCCESS, null, data);
    }

    public static Result fail(ResultCode code, String msg) {
        return new Result(code, msg, null);
    }
}
