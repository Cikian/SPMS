package com.spms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.MenuDTO;
import com.spms.dto.Result;
import com.spms.entity.Menu;
import com.spms.enums.ResultCode;
import com.spms.mapper.MenuMapper;
import com.spms.security.LoginUser;
import com.spms.service.MenuService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.spms.constants.RedisConstants.MENU_LIST;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Result allMenu() {
        String menuListStr = redisTemplate.opsForValue().get(MENU_LIST);
        if (menuListStr != null) {
            return Result.success(JSONObject.parseArray(menuListStr, MenuDTO.class));
        }

        LambdaQueryWrapper<Menu> menuLambdaQueryWrapper = new LambdaQueryWrapper<>();
        menuLambdaQueryWrapper.orderByAsc(Menu::getCreateTime);
        List<Menu> menuList = this.list(menuLambdaQueryWrapper);

        if (menuList.isEmpty()) {
            return Result.success("暂无数据");
        }

        List<MenuDTO> menuDTOList = menuList.stream().map(menu -> {
            MenuDTO menuDTO = new MenuDTO();
            BeanUtils.copyProperties(menu, menuDTO);
            return menuDTO;
        }).toList();

        redisTemplate.opsForValue().set(MENU_LIST, JSONObject.toJSONString(menuDTOList));
        return Result.success(menuDTOList);
    }

    @Override
    public Result addMenu(Menu menu) {
        if (menu == null || StrUtil.isEmpty(menu.getMenuName()) || StrUtil.isEmpty(menu.getPerms()) || menu.getType() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        menu.setStatus(true);

        if (!this.save(menu)) {
            return Result.fail(ResultCode.FAIL.getCode(), "添加失败");
        }
        redisTemplate.delete(MENU_LIST);
        return Result.success("添加成功");
    }
}
