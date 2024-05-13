package com.spms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: Demand
 * @Author Cikian
 * @Package com.spms.entity
 * @Date 2024/5/8 下午9:57
 * @description: SPMS: 需求（工作项），包括史诗、特性、用户故事、任务
 */
@Data
public class Demand {
    @TableId(type = IdType.ASSIGN_ID)
    private Long demandId;
    private Long proId;
    private Integer demandNo;
    private String title;
    private String demandDesc;
    private Integer demandStatus;  // 0-打开，1-进行中，2-完成，-1 -关闭
    private Long headId;  // 需求负责人
    private Integer priority;  // 优先级 0-最低，1-较低，2-普通，3-较高，4-最高
    private Long fatherDemandId;  // 父需求ID
    private Long type;  // 需求类型 1-技术需求，2-功能需求，3-安全需求，4-体验优化
    private Integer workItemType;  // 需求类型 0-史诗，1-特性，2-用户故事，3-任务
    private Long source;  // 需求来源
    private Integer storyPoint;  // 故事点
    private Integer level;  // 需求层级
    private LocalDateTime startTime;  // 开始时间
    private LocalDateTime endTime;  // 结束时间

    @TableField(exist = false)
    private List<Demand> children;  // 子需求

    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    public void addChild(Demand child) {
        if (children != null) {
            this.children.add(child);
        }
    }
}
