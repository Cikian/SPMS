package com.spms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: Defect
 * @Author Cikian
 * @Package com.spms.entity
 * @Date 2024/5/18 上午5:27
 * @description: SPMS: 需求（缺陷）
 */
@Data
public class Defect {
    @TableId(type = IdType.ASSIGN_ID)
    private Long demandId;
    private Long proId;
    private Integer demandNo;
    private String title;
    private String demandDesc;
    private Integer demandStatus;  // 0-新提交，1-处理中，2-已修复，-1 -已拒绝
    private Long headId;  // 需求负责人
    private Integer priority;  // 优先级 0-最低，1-较低，2-普通，3-较高，4-最高
    private Integer severity; // 严重程度 0-建议 1-一般 2-严重 3-致命
        private Integer probability; // 复现概率 0-仅出现一次 1-小概率复现 2-大概率复现 3-必现
    private Long type;  // 需求类型 1-功能问题，2-性能问题，3-接口问题，4-安全问题 5-UI问题 6-兼容性问题 7-易用性问题

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime startTime;  // 开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime endTime;  // 结束时间

    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
