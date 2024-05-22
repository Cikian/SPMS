package com.spms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Title: Meeting
 * @Author Cikian
 * @Package com.spms.entity
 * @Date 2024/5/21 上午4:19
 * @description: SPMS: 会议
 */

@Data
public class Meeting {
    @TableId(type = IdType.ASSIGN_ID)
    private Long meetId;
    private Long proId;
    private String title;
    private String meetingAbstract;
    private String reportFile;

    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
