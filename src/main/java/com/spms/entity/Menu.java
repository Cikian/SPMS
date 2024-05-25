package com.spms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Menu {

    @TableId(type = IdType.ASSIGN_ID)
    private Long menuId;

    //权限名称
    private String menuName;

    //是否启用
    private Boolean status;

    //菜单类型
    private Integer type;

    //权限标识
    private String perms;

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
}
