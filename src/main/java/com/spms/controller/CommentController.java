package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.Comment;
import com.spms.enums.ErrorCode;
import com.spms.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @Autowired
    private CommentService commentService;

    @PostMapping
    public Result addComment(@RequestBody Comment comment) {
        Boolean b = commentService.addComment(comment);
        Integer code = b ? ErrorCode.ADD_SUCCESS : ErrorCode.ADD_FAIL;
        String msg = b ? "添加成功" : "添加失败";
        return new Result(code, msg, null);
    }

    @GetMapping
    public Result getCommentListByWorkItemId(@RequestParam("id") Long workItemId) {
        List<Comment> comments = commentService.getCommentsByWorkItemId(workItemId);
        Integer code = !comments.isEmpty() ? ErrorCode.GET_SUCCESS : ErrorCode.GET_FAIL;
        String msg = !comments.isEmpty() ? "获取成功" : "获取失败";
        return new Result(code, msg, comments);
    }

}
