package com.spms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Title: DemandDependence
 * @Author Cikian
 * @Package com.spms.entity
 * @Date 2024/6/3 下午4:03
 * @description: SPMS: 需求依赖关系
 */

@Data
public class DemandDependence {
    @TableId(type = IdType.ASSIGN_ID)
    private Long dependence;
    private Long beDependenceOn;

    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
