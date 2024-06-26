package com.spms.service;

import com.spms.dto.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface OssService {
    Result uploadFileAvatar(MultipartFile file, HttpServletRequest request) throws IOException;

    Result uploadFileTestReport(MultipartFile file, Long testPlanId, HttpServletRequest request) throws IOException;

    Result uploadFileMeetingReport(MultipartFile file, Long proId, HttpServletRequest request) throws IOException;
}
