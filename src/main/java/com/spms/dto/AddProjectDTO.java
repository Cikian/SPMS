package com.spms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Title: ProjectVo
 * @Author Cikian
 * @Package com.spms.entity.VO
 * @Date 2024/4/10 上午12:22
 * @description: SPMS: 新建项目视图
 */
@Data
public class AddProjectDTO {
    private String proName;
    private String proDesc;  //描述
    private int proStatus; //状态
    private String proFlag; // 标识（英文代号），例如SPMS
    private int proType; // 项目类型: 0：Scrum项目 1：Knaban项目 2：瀑布项目
    private String proCustomer; // 客户名称
    private Long[] proMembersIds; // 项目成员
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime expectedStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime expectedEndTime;
}
