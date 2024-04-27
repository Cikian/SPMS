package com.spms.entity.vo;

import lombok.Data;

/**
 * @Title: ProjectVo
 * @Author Cikian
 * @Package com.spms.entity.VO
 * @Date 2024/4/10 上午12:22
 * @description: SPMS: 新建项目视图
 */
@Data
public class ProjectVo {
    private String proName;
    private String proDesc;  //描述
    private int proStatus; //状态
    private String proFlag; // 标识（英文代号），例如SPMS
    private Long proLeaderId; // 项目负责人
    private int proType; // 项目类型: 0：Scrum项目 1：Knaban项目 2：瀑布项目
    private String proCustomer; // 客户名称
    private ProPeople[] proMembers; // 项目成员
    private ProDevice[] proDevices; // 项目设备
}
