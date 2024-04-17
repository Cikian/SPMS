package com.spms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("role_user")
public class RoleUser {
    private Long roleId;
    private Long userId;
    private Boolean delFlag;
}
