package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.RatedTimeCostDTO;
import com.spms.dto.Result;
import com.spms.entity.Device;
import com.spms.entity.RatedTimeCost;
import com.spms.entity.User;
import com.spms.enums.ResultCode;
import com.spms.mapper.DeviceMapper;
import com.spms.mapper.RatedTimeCostMapper;
import com.spms.mapper.UserMapper;
import com.spms.security.LoginUser;
import com.spms.service.RatedTimeCostService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static com.spms.constants.SystemConstants.DELETE;
import static com.spms.constants.SystemConstants.NOT_DELETE;
import static com.spms.enums.ResourceType.DEVICE;
import static com.spms.enums.ResourceType.EMPLOYEE;

@Service
public class RatedTimeCostServiceImpl extends ServiceImpl<RatedTimeCostMapper, RatedTimeCost> implements RatedTimeCostService {

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result updateCost(RatedTimeCost ratedTimeCost) {
        if (ratedTimeCost == null || ratedTimeCost.getRatedTimeCostId() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        RatedTimeCost one = this.getById(ratedTimeCost.getRatedTimeCostId());
        if (one == null || one.getDelFlag() == DELETE) {
            return Result.fail(ResultCode.FAIL.getCode(), "数据不存在");
        }

        if (ratedTimeCost.getDailyCost() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "请输入日费用");
        }

        if (!this.updateById(ratedTimeCost)) {
            return Result.fail(ResultCode.FAIL.getCode(), "配置失败");
        }
        return Result.success("配置成功");
    }

    @Override
    public Result list(RatedTimeCostDTO ratedTimeCost, Integer page, Integer size) {
        Page<RatedTimeCost> ratedTimeCostPage = new Page<>(page, size);
        Page<RatedTimeCostDTO> ratedTimeCostDTOPage = new Page<>();

        BigDecimal minDailyCost = null;
        BigDecimal maxDailyCost = null;
        if (ratedTimeCost.getDailyCost() != null) {
            BigDecimal dailyCost = ratedTimeCost.getDailyCost();
            minDailyCost = dailyCost.subtract(new BigDecimal(10));
            maxDailyCost = dailyCost.add(new BigDecimal(10));
        }

        BigDecimal minMonthlyCost = null;
        BigDecimal maxMonthlyCost = null;
        if (ratedTimeCost.getMonthlyCost() != null) {
            BigDecimal monthlyCost = ratedTimeCost.getMonthlyCost();
            minMonthlyCost = monthlyCost.subtract(new BigDecimal(100));
            maxMonthlyCost = monthlyCost.add(new BigDecimal(100));
        }

        List<Long> ids;
        if (Objects.equals(ratedTimeCost.getResourceType(), EMPLOYEE.getCode())) {
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.like(!Objects.isNull(ratedTimeCost.getResourceName()), User::getUserName, ratedTimeCost.getResourceName());
            List<User> userList = userMapper.selectList(userLambdaQueryWrapper);
            ids = userList.stream().map(User::getUserId).toList();
        } else {
            LambdaQueryWrapper<Device> deviceLambdaQueryWrapper = new LambdaQueryWrapper<>();
            deviceLambdaQueryWrapper.like(!Objects.isNull(ratedTimeCost.getResourceName()), Device::getDevName, ratedTimeCost.getResourceName());
            List<Device> deviceList = deviceMapper.selectList(deviceLambdaQueryWrapper);
            ids = deviceList.stream().map(Device::getDevId).toList();
        }

        LambdaQueryWrapper<RatedTimeCost> ratedTimeCostLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ratedTimeCostLambdaQueryWrapper
                .in(RatedTimeCost::getResourceId, ids)
                .eq(RatedTimeCost::getResourceType, ratedTimeCost.getResourceType())
                .between(!Objects.isNull(ratedTimeCost.getDailyCost()), RatedTimeCost::getDailyCost, minDailyCost, maxDailyCost)
                .between(!Objects.isNull(ratedTimeCost.getMonthlyCost()), RatedTimeCost::getMonthlyCost, minMonthlyCost, maxMonthlyCost)
                .eq(RatedTimeCost::getDelFlag, NOT_DELETE);
        this.page(ratedTimeCostPage, ratedTimeCostLambdaQueryWrapper);

        if (ratedTimeCostPage.getRecords().isEmpty()) {
            return Result.fail(ResultCode.FAIL.getCode(), "暂无数据");
        }

        BeanUtils.copyProperties(ratedTimeCostPage, ratedTimeCostDTOPage, "records");

        List<RatedTimeCostDTO> ratedTimeCostDTOList = ratedTimeCostPage.getRecords().stream().map(this::ConvertDTOAndGetResourceName).toList();

        ratedTimeCostDTOPage.setRecords(ratedTimeCostDTOList);
        return Result.success(ratedTimeCostDTOPage);
    }

    @Override
    public Result queryById(Long ratedTimeCostId) {
        if (ratedTimeCostId == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        RatedTimeCost ratedTimeCost = this.getById(ratedTimeCostId);

        if (ratedTimeCost == null || ratedTimeCost.getDelFlag() == DELETE) {
            return Result.fail(ResultCode.FAIL.getCode(), "数据不存在");
        }

        return Result.success(ConvertDTOAndGetResourceName(ratedTimeCost));
    }

    private RatedTimeCostDTO ConvertDTOAndGetResourceName(RatedTimeCost ratedTimeCost) {
        RatedTimeCostDTO ratedTimeCostDTO = new RatedTimeCostDTO();
        BeanUtils.copyProperties(ratedTimeCost, ratedTimeCostDTO);

        Long resourceId = ratedTimeCost.getResourceId();
        Integer resourceType = ratedTimeCost.getResourceType();
        String resourceName = null;

        if (Objects.equals(resourceType, DEVICE.getCode())) {
            resourceName = deviceMapper.selectById(resourceId).getDevName();
        } else if (Objects.equals(resourceType, EMPLOYEE.getCode())) {
            resourceName = userMapper.selectById(resourceId).getNickName();
        }
        ratedTimeCostDTO.setResourceName(resourceName);

        return ratedTimeCostDTO;
    }
}
