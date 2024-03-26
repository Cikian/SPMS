package com.spms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_menu")
public class Menu {

    @TableId(type = IdType.ASSIGN_ID)
    private Long menuId;

    //权限名称
    private String menuName;

    //路由地址
    private String path;

    //组件路径
    private String component;

    //是否可见
    private Boolean visible;

    //是否启用
    private Boolean status;

    //权限标识
    private String perms;

    //图标地址
    private String icon;

    //创建人
    private Long createBy;

    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    //更新人
    private Long updateBy;

    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    //删除标记
    private Boolean delFlag;

    //备注
    private String remark;
}
