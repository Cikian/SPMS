package com.spms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Role {

    @TableId(type = IdType.ASSIGN_ID)
    private Long roleId;

    //角色名称
    private String roleName;

    //是否禁用
    private Boolean status;

    private Boolean isSystem;

    //创建人
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    //更新人
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    //删除标记
    private Boolean delFlag;

    //备注
    private String remark;
}
