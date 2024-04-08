package com.spms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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

    private Long proLeaderId; // 项目负责人

    private int proType; // 项目类型: 0：Scrum项目 1：Knaban项目 2：瀑布项目

    private String proCustomer; // 客户名称

    private String proStartTime;

    private String proEndTime;

    private String proCreateTime;

    private String proUpdateTime;

}
