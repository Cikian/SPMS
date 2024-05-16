package com.spms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Title: DemandActive
 * @Author Cikian
 * @Package com.spms.entity
 * @Date 2024/5/16 上午2:04
 * @description: SPMS: 需求活动记录
 */

@Data
public class DemandActive {
    @TableId(type = IdType.ASSIGN_ID)
    private Long activeId;
    private Long demandId;
    private String activeType; //活动类型：创建、修改
    private String activeContent; //活动内容：修改的具体内容
    private String fromActive;
    private String toActive;

    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
