package com.spms.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spms.config.OSSConfig;
import com.spms.dto.Result;
import com.spms.entity.Meeting;
import com.spms.entity.TestPlan;
import com.spms.entity.TestReport;
import com.spms.entity.User;
import com.spms.enums.ResultCode;
import com.spms.mapper.ProjectMapper;
import com.spms.mapper.TestPlanMapper;
import com.spms.mapper.TestReportMapper;
import com.spms.mapper.UserMapper;
import com.spms.security.LoginUser;
import com.spms.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.spms.constants.RedisConstants.*;
import static com.spms.constants.SystemConstants.*;
import static com.spms.enums.TestReportApprovalStatus.UNAUDITED;

@Service
public class OssServiceImpl implements OssService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TestReportMapper testReportMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TestPlanMapper testPlanMapper;

    @Autowired
    private ProjectService projectService;

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

    @Override
    public Result uploadFileTestReport(MultipartFile file, Long testPlanId, HttpServletRequest request) throws IOException {
        // 判断是否有权限上传测试报告
        TestPlan testPlan = testPlanMapper.selectById(testPlanId);
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        if (!Objects.equals(userId, testPlan.getHead())) {
            return Result.fail(ResultCode.FAIL.getCode(), "无权限上传测试报告");
        }
        // 判断是否已经上传过测试报告
        LambdaQueryWrapper<TestReport> testReportLambdaQueryWrapper = new LambdaQueryWrapper<>();
        testReportLambdaQueryWrapper.eq(TestReport::getTestPlanId, testPlanId);
        TestReport testReport = testReportMapper.selectOne(testReportLambdaQueryWrapper);
        // 没有上传过测试报告
        if (testReport == null) {
            String url = uploadFileAndReturnUrl(file, UPLOAD_TEST_REPORT, null);
            TestReport newTestReport = new TestReport();
            newTestReport.setTestPlanId(testPlanId);
            newTestReport.setReportFile(url);
            newTestReport.setTestReportName(file.getOriginalFilename());
            newTestReport.setReviewStatus(UNAUDITED.getCode());
            newTestReport.setDelFlag(NOT_DELETE);
            testReportMapper.insert(newTestReport);
            notificationService.addNotification(testPlan.getCreateBy(), testPlan.getPlanName() + "(" + file.getOriginalFilename() + ")",
                    "测试报告已上传，请尽快审核");
            return Result.success("上传成功", url);
        }

        if (!testReport.getDelFlag()) {
            return Result.fail(ResultCode.FAIL.getCode(), "该测试计划已包含测试报告，如需替换请删除后上传");
        }
        // 获取原测试报告文件名
        String originTestReportUrl = testReport.getReportFile();
        String[] split = originTestReportUrl.split("/");
        String originTestReportName = split[split.length - 1];
        String url = uploadFileAndReturnUrl(file, UPLOAD_TEST_REPORT, UPLOAD_TEST_REPORT + originTestReportName);

        testReport.setTestReportName(file.getOriginalFilename());
        testReport.setReportFile(url);
        testReport.setDelFlag(NOT_DELETE);
        testReport.setReviewStatus(UNAUDITED.getCode());
        testReportMapper.updateById(testReport);
        notificationService
                .addNotification(testPlan.getCreateBy(), testPlan.getPlanName() + "(" + file.getOriginalFilename() + ")", "测试报告已上传，请尽快审核");
        return Result.success("上传成功", url);
    }

    @Override
    public Result uploadFileMeetingReport(MultipartFile file, Long proId, HttpServletRequest request) throws IOException {

        if (!projectService.judgeIsProHeader(proId)) {
            return Result.fail(ResultCode.FAIL.getCode(), "无权限上传会议记录");
        }
        String url = uploadFileAndReturnUrl(file, UPLOAD_MEETING_REPORT, null);

        return Result.success("上传成功", url);
    }

    private static String uploadFileAndReturnUrl(MultipartFile file, String type, String originFileName) throws IOException {
        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(OSSConfig.END_POINT, OSSConfig.ACCESS_KEY_ID, OSSConfig.ACCESS_KEY_SECRET);
        // 生成文件名
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String fileName = type + uuid + file.getOriginalFilename();
        // 上传文件
        PutObjectRequest putObjectRequest = new PutObjectRequest(OSSConfig.BUCKET_NAME, fileName, new ByteArrayInputStream(file.getBytes()));
        try {
            ossClient.putObject(putObjectRequest);
            if (originFileName != null) {
                ossClient.deleteObject(OSSConfig.BUCKET_NAME, originFileName);
            }
        } catch (OSSException | ClientException e) {
            throw new RuntimeException("上传失败");
        } finally {
            ossClient.shutdown();
        }
        // 返回文件URL
        return "https://" + OSSConfig.BUCKET_NAME + "." + OSSConfig.END_POINT + "/" + fileName;
    }
}
