package com.spms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spms.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
    List<String> selectUserHasPermission(Long userId);
}
