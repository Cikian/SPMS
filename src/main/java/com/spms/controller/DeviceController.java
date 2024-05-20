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
    @PreAuthorize("hasRole('system_admin')")
    public Result add(@RequestBody Device device) {
        return deviceService.add(device);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('system_admin')")
    public Result delete(@RequestBody Long[] ids) {
        return deviceService.delete(ids);
    }

    @PostMapping("/list")
    @PreAuthorize("hasRole('system_admin')")
    public Result list(@RequestBody Device device,
                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return deviceService.list(device, page, size);
    }

    @GetMapping("/queryById/{deviceId}")
    @PreAuthorize("hasRole('system_admin')")
    public Result queryById(@PathVariable("deviceId") Long deviceId) {
        return deviceService.queryById(deviceId);
    }

    @PostMapping("/updateStatus")
    @PreAuthorize("hasRole('system_admin')")
    public Result updateStatus(@RequestBody Device device) {
        return deviceService.updateStatus(device);
    }

    @PostMapping("/updateInfo")
    @PreAuthorize("hasRole('system_admin')")
    public Result updateInfo(@RequestBody Device device) {
        return deviceService.updateInfo(device);
    }

    @PostMapping("/releaseAllResource/{proId}")
    @PreAuthorize("hasRole('system_admin')")
    public Result releaseAllResource(@PathVariable("proId") Long proId) {
        return deviceService.releaseAllResource(proId);
    }

    @GetMapping("/queryCanAddToProjectDevice/{proId}")
    public Result queryCanAddToProjectDevice(@PathVariable("proId") Long proId) {
        return deviceService.queryCanAddToProjectDevice(proId);
    }
}
