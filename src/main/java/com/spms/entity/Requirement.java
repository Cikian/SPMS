package com.spms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.List;

@Data
public class Requirement {
    @TableId(type = IdType.ASSIGN_ID)
    private Long requirementId;

    @TableField(exist = false)
    List<Requirement> sonRequirements;
}
