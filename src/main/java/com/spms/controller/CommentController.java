package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.Comment;
import com.spms.enums.ErrorCode;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Title: CommentController
 * @Author Cikian
 * @Package com.spms.controller
 * @Date 2024/5/15 下午5:50
 * @description: SPMS: 留言
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @PostMapping
    public Result addComment(@RequestBody Comment comment) {
        System.out.println("addComment");
        System.out.println(comment);

        Integer code = ErrorCode.ADD_SUCCESS;
        String msg = "添加成功";
        return new Result(code, msg, null);
    }

}
