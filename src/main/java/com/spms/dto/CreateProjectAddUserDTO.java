package com.spms.dto;

import lombok.Data;

/**
 * @Title: CreateProjectAddUserDTO
 * @Author Cikian
 * @Package com.spms.dto
 * @Date 2024/5/3 下午2:57
 * @description: SPMS: 创建项目时选择项目成员的成员视图类
 */
@Data
public class CreateProjectAddUserDTO {
    private Long userId;
    private String userName;
    private String avatar;
    private String position;
}
