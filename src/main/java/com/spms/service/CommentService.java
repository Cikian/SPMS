package com.spms.service;

import com.spms.entity.Comment;

import java.util.List;

/**
 * @Title: CommentService
 * @Author Cikian
 * @Package com.spms.service
 * @Date 2024/5/15 下午6:40
 * @description: SPMS: 留言
 */
public interface CommentService {
    List<Comment> getCommentsByWorkItemId(Long workItemId);

    Boolean addComment(Comment comment);
}
