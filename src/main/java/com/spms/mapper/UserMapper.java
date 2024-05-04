package com.spms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spms.dto.CreateProjectAddUserDTO;
import com.spms.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    List<CreateProjectAddUserDTO> queryCanAddToProject();
}
