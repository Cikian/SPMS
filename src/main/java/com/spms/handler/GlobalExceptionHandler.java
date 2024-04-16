package com.spms.handler;

import com.spms.dto.Result;
import com.spms.enums.ResultCode;
import io.lettuce.core.RedisException;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler({MailSendException.class, SQLException.class, SQLSyntaxErrorException.class, RedisException.class})
    public Result handleRuntimeException(Exception e) {
        return Result.fail(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "服务器异常");
    }
}
