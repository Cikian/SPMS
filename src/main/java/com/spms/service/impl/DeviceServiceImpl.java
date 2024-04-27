package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.DeviceDTO;
import com.spms.dto.Result;
import com.spms.entity.Device;
import com.spms.enums.DeviceStatus;
import com.spms.enums.DeviceType;
import com.spms.enums.ResultCode;
import com.spms.mapper.DeviceMapper;
import com.spms.security.LoginUser;
import com.spms.service.DeviceService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.spms.constants.SystemConstants.NOT_DELETE;

@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Result add(Device device) {
        if (device == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        if (device.getDevName() == null || device.getDevName().isEmpty()) {
            return Result.fail(ResultCode.FAIL.getCode(), "设备名称不能为空");
        }

        if (device.getType() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "设备类型不能为空");
        }

        if (!DeviceType.contains(device.getType())) {
            return Result.fail(ResultCode.FAIL.getCode(), "设备类型错误");
        }

        if (device.getCost() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "设备价格不能为空");
        }

        LambdaQueryWrapper<Device> deviceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deviceLambdaQueryWrapper.eq(Device::getDevName, device.getDevName())
                .eq(Device::getDelFlag, NOT_DELETE);
        Device one = this.getOne(deviceLambdaQueryWrapper);
        if (one != null) {
            return Result.fail(ResultCode.FAIL.getCode(), "设备名称已存在");
        }

        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        device.setCreateBy(loginUser.getUser().getUserId());
        device.setUpdateBy(loginUser.getUser().getUserId());
        device.setStatus(DeviceStatus.NORMAL.getCode());
        device.setDelFlag(NOT_DELETE);

        boolean isSuccess = this.save(device);

        if (!isSuccess) {
            return Result.fail(ResultCode.FAIL.getCode(), "添加失败");
        }

        return Result.success("添加成功");
    }

    @Override
    public Result delete(Long[] ids) {
        return null;
    }

    @Override
    public Result list(Device device, Integer page, Integer size) {
        Page<Device> devicePage = new Page<>(page, size);
        Page<DeviceDTO> deviceDTOPage = new Page<>();

        BigDecimal minCost = null;
        BigDecimal maxCost = null;
        if (device.getCost() != null) {
            BigDecimal cost = device.getCost();
            minCost = cost.subtract(new BigDecimal(100));
            maxCost = cost.add(new BigDecimal(100));
        }

        LocalDateTime minPurchaseDate = null;
        LocalDateTime maxPurchaseDate = null;
        if (device.getPurchaseDate() != null) {
            minPurchaseDate = device.getPurchaseDate().withHour(0).withMinute(0).withSecond(0).withNano(0);
            maxPurchaseDate = device.getPurchaseDate().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        }

        LambdaQueryWrapper<Device> deviceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deviceLambdaQueryWrapper
                .like((!Objects.isNull(device.getDevName())), Device::getDevName, device.getDevName())
                .eq((!Objects.isNull(device.getStatus())), Device::getStatus, device.getStatus())
                .eq((!Objects.isNull(device.getType())), Device::getType, device.getType())
                .ge((!Objects.isNull(device.getCost())), Device::getCost, minCost)
                .le((!Objects.isNull(device.getCost())), Device::getCost, maxCost)
                .between((!Objects.isNull(device.getPurchaseDate())), Device::getPurchaseDate, minPurchaseDate, maxPurchaseDate)
                .eq(Device::getDelFlag, NOT_DELETE);
        this.page(devicePage, deviceLambdaQueryWrapper);

        if (devicePage.getRecords().isEmpty()) {
            return Result.fail(ResultCode.FAIL.getCode(), "暂无数据");
        }

        BeanUtils.copyProperties(devicePage, deviceDTOPage, "records");

        List<DeviceDTO> deviceDTOList = devicePage.getRecords().stream().map(item -> {
            DeviceDTO deviceDTO = new DeviceDTO();
            BeanUtils.copyProperties(item, deviceDTO);
            return deviceDTO;
        }).toList();
        deviceDTOPage.setRecords(deviceDTOList);

        return Result.success(deviceDTOPage);
    }

    @Override
    public Result queryById(Long deviceId) {
        if (deviceId == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LambdaQueryWrapper<Device> deviceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deviceLambdaQueryWrapper.eq(Device::getDevId, deviceId)
                .eq(Device::getDelFlag, NOT_DELETE);
        Device device = this.getOne(deviceLambdaQueryWrapper);
        if (device == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "设备不存在");
        }

        DeviceDTO deviceDTO = new DeviceDTO();
        BeanUtils.copyProperties(device, deviceDTO);

        return Result.success(deviceDTO);
    }

    @Override
    public Result updateStatus(Device device) {
        if (device == null || device.getDevId() == null || device.getStatus() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        Device device1 = this.getById(device.getDevId());
        if (device1 == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "设备不存在");
        }

        if (device1.getStatus().equals(device.getStatus())) {
            return Result.fail(ResultCode.FAIL.getCode(), "状态未改变");
        }

        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        device.setUpdateBy(loginUser.getUser().getUserId());

        boolean isSuccess = this.updateById(device);

        if (!isSuccess) {
            return Result.fail(ResultCode.FAIL.getCode(), "更新失败");
        }
        return Result.success("更新成功");
    }

    @Override
    public Result updateInfo(Device device) {
        if (device == null || device.getDevId() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        Device device1 = this.getById(device.getDevId());
        if (device1 == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "设备不存在");
        }

        if (!device1.getCost().equals(device.getCost()) || !device1.getPurchaseDate().equals(device.getPurchaseDate())) {
            return Result.fail(ResultCode.FAIL.getCode(), "非法操作");
        }

        if (device.getDevName() == null || device.getDevName().isEmpty()) {
            return Result.fail(ResultCode.FAIL.getCode(), "设备名称不能为空");
        }

        if (device.getType() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "设备类型不能为空");
        }

        if (!DeviceType.contains(device.getType())) {
            return Result.fail(ResultCode.FAIL.getCode(), "设备类型错误");
        }

        //判断设备名称是否重复
        LambdaQueryWrapper<Device> deviceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deviceLambdaQueryWrapper.eq(Device::getDevName, device.getDevName())
                .ne(Device::getDevId, device.getDevId())
                .eq(Device::getDelFlag, NOT_DELETE);
        Device one = this.getOne(deviceLambdaQueryWrapper);
        if (one != null) {
            return Result.fail(ResultCode.FAIL.getCode(), "设备名称已存在");
        }

        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        device.setUpdateBy(loginUser.getUser().getUserId());

        boolean isSuccess = this.updateById(device);

        if (!isSuccess) {
            return Result.fail(ResultCode.FAIL.getCode(), "更新失败");
        }
        return Result.success("更新成功");
    }
}
