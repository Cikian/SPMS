package com.spms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Notification {

    @TableId(type = IdType.ASSIGN_ID)
    private Long notificationId;

    private Long receiverId;

    private String title;

    private String content;

    private Boolean readFlag;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
