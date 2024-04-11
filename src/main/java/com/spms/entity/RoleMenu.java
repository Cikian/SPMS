package com.spms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("role_menu")
public class RoleMenu {
    private Long roleId;
    private Long menuId;
    private Boolean delFlag;
}
