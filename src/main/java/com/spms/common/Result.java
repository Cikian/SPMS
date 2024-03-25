package com.spms.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import static com.spms.common.ResultCode.SUCCESS;

@Data
@AllArgsConstructor
public class Result {

    private Integer code;

    private String message;

    private Object data;

    private Result() {}

    public static Result success() {
        return new Result(SUCCESS.ordinal(), null, null);
    }

    public static Result success(String msg, Object data) {
        return new Result(SUCCESS.ordinal(), msg, data);
    }

    public static Result success(Object data) {
        return new Result(SUCCESS.ordinal(), null, data);
    }

    public static Result fail(Integer code, String msg) {
        return new Result(code, msg, null);
    }
}
