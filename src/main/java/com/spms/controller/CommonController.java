package com.spms.controller;

import com.spms.dto.Result;
import com.spms.service.OssService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
        return ossService.uploadFileAvatar(file,request);
    }
}
