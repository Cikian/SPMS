package com.spms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spms.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Title: CommentMapper
 * @Author Cikian
 * @Package com.spms.mapper
 * @Date 2024/5/15 下午6:38
 * @description: SPMS: 留言
 */

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
