package com.spms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QualityTarget {
    @TableId(type = IdType.ASSIGN_ID)
    private Long qualityTargetId;

    private String targetName;

    //质量目标特性
    private Integer qualityAttribute;

    //目标值 例：达到xx水平
    private String targetValue;

    //目标优先级
    private Integer priority;

    //目标度量指标 例：平均xx即可满足
    private String metric;

    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private Boolean delFlag;
}
