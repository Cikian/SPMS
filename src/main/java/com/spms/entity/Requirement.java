package com.spms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Requirement {
    @TableId(type = IdType.ASSIGN_ID)
    private Long requirementId;
}
