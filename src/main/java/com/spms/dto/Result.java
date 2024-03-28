package com.spms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import static com.spms.enums.ResultCode.SUCCESS;

@Data
public class Result {

    private Integer code;

    private String message;

    private Object data;

    private Result() {
    }

    public Result(Integer code) {
        this.code = code;
    }

    public Result(Integer code, Object data) {
        this.code = code;
        this.data = data;
    }

    public Result(Integer code, String msg){
        this.code = code;
        this.message = msg;
    }

    public Result(Integer code, String msg, Object data) {
        this.code = code;
        this.message = msg;
        this.data = data;
    }

    public static Result success() {
        return new Result(SUCCESS.getCode());
    }

    public static Result success(String msg) {
        return new Result(SUCCESS.getCode(), msg);
    }

    public static Result success(Object data) {
        return new Result(SUCCESS.getCode(), data);
    }

    public static Result success(String msg, Object data) {
        return new Result(SUCCESS.getCode(), msg, data);
    }

    public static Result fail(Integer code, String msg) {
        return new Result(code, msg);
    }
}
