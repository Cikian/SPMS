package com.spms.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: Project
 * @Author Cikian
 * @Package com.spms.entity
 * @Date 2024/4/1 14:16
 * @description: 项目信息
 */

@Data
@TableName("project")
public class Project {
    @TableId(type = IdType.ASSIGN_ID)
    private Long proId;
    private String proName;
    private String proDesc;  //描述
    private int proStatus; //状态
    private String proFlag; // 标识（英文代号），例如SPMS
    @TableField(fill = FieldFill.INSERT)
    private Long createBy; // 项目负责人
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy; // 项目负责人
    private int proType; // 项目类型: 0：Scrum项目 1：Knaban项目 2：瀑布项目
    private String proCustomer; // 客户名称
    private LocalDateTime expectedStartTime;
    private LocalDateTime expectedEndTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<Demand> demands;
}
