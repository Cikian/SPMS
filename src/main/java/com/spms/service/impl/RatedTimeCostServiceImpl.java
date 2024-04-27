package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.RatedTimeCostDTO;
import com.spms.dto.Result;
import com.spms.entity.RatedTimeCost;
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
    public Result add(RatedTimeCost ratedTimeCost) {
        if (ratedTimeCost == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        if (ratedTimeCost.getResourceId() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "请选择资源");
        }

        if (ratedTimeCost.getResourceType() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "请选择资源类型");
        }

        if (ratedTimeCost.getDailyCost() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "请输入日费用");
        }

        if (ratedTimeCost.getMonthlyCost() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "请输入月费用");
        }

        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ratedTimeCost.setCreateBy(loginUser.getUser().getUserId());
        ratedTimeCost.setUpdateBy(loginUser.getUser().getUserId());
        ratedTimeCost.setDelFlag(NOT_DELETE);

        boolean isSuccess = this.save(ratedTimeCost);

        if (!isSuccess) {
            return Result.fail(ResultCode.FAIL.getCode(), "添加失败");
        }
        return Result.success("配置成功");
    }

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

        if (ratedTimeCost.getMonthlyCost() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "请输入月费用");
        }

        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ratedTimeCost.setUpdateBy(loginUser.getUser().getUserId());

        boolean isSuccess = this.updateById(ratedTimeCost);

        if (!isSuccess) {
            return Result.fail(ResultCode.FAIL.getCode(), "配置失败");
        }
        return Result.success("配置成功");
    }

    @Override
    public Result list(RatedTimeCost ratedTimeCost, Integer page, Integer size) {
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

        LambdaQueryWrapper<RatedTimeCost> ratedTimeCostLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ratedTimeCostLambdaQueryWrapper
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


    @Override
    public Result delete(Long[] ids) {
        return null;
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
            resourceName = userMapper.selectById(resourceId).getUserName();
        }
        ratedTimeCostDTO.setResourceName(resourceName);

        return ratedTimeCostDTO;
    }
}
