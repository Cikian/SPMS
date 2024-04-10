package com.spms.dto;

import lombok.Data;

@Data
public class RoleDTO {
    private Long roleId;
    private String roleName;
    private Boolean status;
    private String remark;
}
