package com.spms.controller;

import com.spms.dto.DeviceDTO;
import com.spms.dto.Result;
import com.spms.dto.RoleDTO;
import com.spms.entity.Device;
import com.spms.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('device:add') || hasRole('system_admin')")
    public Result add(@RequestBody Device device) {
        return deviceService.add(device);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('device:delete') || hasRole('system_admin')")
    public Result delete(@RequestBody Long[] ids) {
        return deviceService.delete(ids);
    }

    @PostMapping("/updateStatus")
    @PreAuthorize("hasAuthority('device:update:status') || hasRole('system_admin')")
    public Result updateStatus(@RequestBody Device device) {
        return deviceService.updateStatus(device);
    }

    @PostMapping("/updateInfo")
    @PreAuthorize("hasAuthority('device:update:info') || hasRole('system_admin')")
    public Result updateInfo(@RequestBody Device device) {
        return deviceService.updateInfo(device);
    }

    @PostMapping("/list")
    public Result list(@RequestBody Device device,
                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return deviceService.list(device, page, size);
    }

    @GetMapping("/queryById/{deviceId}")
    public Result queryById(@PathVariable("deviceId") Long deviceId) {
        return deviceService.queryById(deviceId);
    }

    //查询可以添加到项目的设备
    @GetMapping("/queryCanAddToProjectDevice/{proId}")
    public Result queryCanAddToProjectDevice(@PathVariable("proId") Long proId) {
        return deviceService.queryCanAddToProjectDevice(proId);
    }
}
