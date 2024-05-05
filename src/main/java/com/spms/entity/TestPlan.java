package com.spms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestPlan {

    @TableId(type = IdType.ASSIGN_ID)
    private Long testPlanId;

    private Long requirementId;

    private String planName;

    //计划内容
    private String planContent;

    //计划进度
    private String schedule;

    //负责人
    private Long head;

    @TableField(fill = FieldFill.INSERT)

    private Long createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
