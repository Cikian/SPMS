package com.spms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @Title: ProAnnouncement
 * @Author Cikian
 * @Package com.spms.entity
 * @Date 2024/5/6 下午4:43
 * @description: SPMS: 项目公告
 */
@Data
public class ProAnnouncement {
    @TableId(type = IdType.ASSIGN_ID)
    private Long annoId;
    private Long proId;
    private String content;
}
