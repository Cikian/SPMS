package com.spms.service;

import com.spms.dto.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface OssService {
    Result uploadFileAvatar(MultipartFile file, HttpServletRequest request) throws IOException;
}
