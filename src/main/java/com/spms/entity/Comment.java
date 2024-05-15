package com.spms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * @Title: Comment
 * @Author Cikian
 * @Package com.spms.entity
 * @Date 2024/5/15 下午4:56
 * @description: SPMS: 评论（留言）
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @TableId(type = IdType.ASSIGN_ID)
    private Long commentId;
    private Long workItemId;
    private Long userId;
    private String avatar;
    private String nickName;
    private String toCommentId; // 评论的评论, 一级评论的commentId为0
    private String content;
    private Long toUserId; // 评论的评论的接收者
    private String toUserNickName; // 评论的评论的接收者

    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
