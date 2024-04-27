package com.spms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RatedTimeCost {

    @TableId(type = IdType.ASSIGN_ID)
    private Long ratedTimeCostId;

    private Long resourceId;

    private Integer resourceType;

    private BigDecimal dailyCost;

    private BigDecimal monthlyCost;

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
}
