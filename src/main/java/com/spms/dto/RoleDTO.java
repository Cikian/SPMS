package com.spms.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoleDTO {
    private Long roleId;
    private String roleName;
    private Boolean status;
    private String remark;
    private LocalDateTime createTime;
}
