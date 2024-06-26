package com.spms.controller;

import com.spms.dto.Result;
import com.spms.service.OssService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private OssService ossService;

    @PostMapping("/upload/avatar")
    public Result uploadFileAvatar(MultipartFile file, HttpServletRequest request) throws IOException {
        return ossService.uploadFileAvatar(file, request);
    }

    @PostMapping("/upload/testReport")
    @PreAuthorize("hasAuthority('testReport:add') ||  hasRole('system_admin')")
    public Result uploadFileTestReport(MultipartFile file, Long testPlanId, HttpServletRequest request) throws IOException {
        return ossService.uploadFileTestReport(file, testPlanId, request);
    }

    @PostMapping("/upload/meetingReport")
    public Result uploadFileMeetingReport(MultipartFile file, Long proId, HttpServletRequest request) throws IOException {
        return ossService.uploadFileMeetingReport(file, proId, request);
    }
}
