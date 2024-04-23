package com.spms.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.spms.config.OSSConfig;
import com.spms.dto.Result;
import com.spms.entity.User;
import com.spms.enums.ResultCode;
import com.spms.mapper.UserMapper;
import com.spms.security.LoginUser;
import com.spms.service.OssService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.spms.constants.RedisConstants.*;
import static com.spms.constants.SystemConstants.UPLOAD_AVATAR;

@Service
public class OssServiceImpl implements OssService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result uploadFileAvatar(MultipartFile file, HttpServletRequest request) throws IOException {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = loginUser.getUser();
        Long userId = user.getUserId();

        String isUpdate = redisTemplate.opsForValue().get(UPDATE_AVATAR + userId);
        if (isUpdate != null) {
            return Result.fail(ResultCode.FAIL.getCode(), "请勿频繁操作");
        }

        String originAvatarUrl = user.getAvatar();
        String[] split = originAvatarUrl.split("/");
        String originAvatarName = split[split.length - 1];
        String url = uploadFileAndReturnUrl(file, UPLOAD_AVATAR, UPLOAD_AVATAR + originAvatarName);

        user.setAvatar(url);
        userMapper.updateById(user);

        redisTemplate.opsForValue().set(USER_LOGIN + userId, JSONObject.toJSONString(loginUser), USER_LOGIN_TTL, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(UPDATE_AVATAR + userId, "1", UPDATEAVATAR_TTL, TimeUnit.MINUTES);
        return Result.success("上传成功", url);
    }

    private static String uploadFileAndReturnUrl(MultipartFile file, String type, String originAvatarName) throws IOException {
        OSS ossClient = new OSSClientBuilder().build(OSSConfig.END_POINT, OSSConfig.ACCESS_KEY_ID, OSSConfig.ACCESS_KEY_SECRET);

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String fileName = type + uuid + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = new PutObjectRequest(OSSConfig.BUCKET_NAME, fileName, new ByteArrayInputStream(file.getBytes()));
        try {
            ossClient.putObject(putObjectRequest);
            ossClient.deleteObject(OSSConfig.BUCKET_NAME, originAvatarName);
        } catch (OSSException e) {
            System.out.println(e.getErrorCode());
        } catch (ClientException e) {
            System.out.println(e.getErrorCode());
        } finally {
            ossClient.shutdown();
        }
        return "https://" + OSSConfig.BUCKET_NAME + "." + OSSConfig.END_POINT + "/" + fileName;
    }
}
