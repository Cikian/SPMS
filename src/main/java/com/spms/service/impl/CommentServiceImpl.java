package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spms.entity.Comment;
import com.spms.entity.User;
import com.spms.mapper.CommentMapper;
import com.spms.mapper.UserMapper;
import com.spms.security.LoginUser;
import com.spms.service.CommentService;
import com.spms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Title: CommentServiceImpl
 * @Author Cikian
 * @Package com.spms.service.impl
 * @Date 2024/5/15 下午6:41
 * @description: SPMS: 留言
 */
@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Comment> getCommentsByWorkItemId(Long workItemId) {
        LambdaQueryWrapper<Comment> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Comment::getWorkItemId, workItemId);
        lqw.orderByDesc(Comment::getCreateTime);
        List<Comment> commentByWorkItemId = commentMapper.selectList(lqw);

        for (int i = 0; i < commentByWorkItemId.size(); i++) {
            Comment comment = commentByWorkItemId.get(i);
            User user = userMapper.selectById(comment.getUserId());

            User toUser = new User();

            if (comment.getToUserId() != null) {
                toUser = userMapper.selectById(comment.getToUserId());
            }
            if (toUser != null) {
                comment.setToUserNickName(toUser.getNickName());
            }
            comment.setNickName(user.getNickName());
            comment.setAvatar(user.getAvatar());

            commentByWorkItemId.set(i, comment);
        }
        return commentByWorkItemId;
    }

    @Override
    public Boolean addComment(Comment comment) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        comment.setUserId(userId);
        return commentMapper.insert(comment) > 0;
    }
}
