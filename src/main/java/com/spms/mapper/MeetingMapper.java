package com.spms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spms.entity.Meeting;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Title: MeetingMapper
 * @Author Cikian
 * @Package com.spms.mapper
 * @Date 2024/5/21 上午4:23
 * @description: SPMS:
 */

@Mapper
public interface MeetingMapper extends BaseMapper<Meeting> {
}
