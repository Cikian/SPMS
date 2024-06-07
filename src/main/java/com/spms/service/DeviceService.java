package com.spms.service;

import com.spms.dto.Result;
import com.spms.entity.Device;

public interface DeviceService {
    Result add(Device device);

    Result delete(Long[] ids);

    Result updateInfo(Device device);

    Result list(Device device, Integer page, Integer size);

    Result queryById(Long deviceId);

    Result updateStatus(Device device);

    Result queryCanAddToProjectDevice(Long proId);
}
