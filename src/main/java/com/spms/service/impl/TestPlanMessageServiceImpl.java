package com.spms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.Result;
import com.spms.dto.TestPlanMessageDTO;
import com.spms.entity.TestPlanMessage;
import com.spms.entity.User;
import com.spms.enums.ResultCode;
import com.spms.mapper.TestPlanMessageMapper;
import com.spms.mapper.UserMapper;
import com.spms.security.LoginUser;
import com.spms.service.TestPlanMessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.spms.constants.SystemConstants.NOT_DELETE;

@Service
public class TestPlanMessageServiceImpl extends ServiceImpl<TestPlanMessageMapper, TestPlanMessage> implements TestPlanMessageService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result add(TestPlanMessage testPlanMessage) {
        if (Objects.isNull(testPlanMessage.getTestPlanId())) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        if (StrUtil.isEmpty(testPlanMessage.getContent())) {
            return Result.fail(ResultCode.FAIL.getCode(), "留言内容不能为空");
        }

        testPlanMessage.setDelFlag(NOT_DELETE);

        if (!this.save(testPlanMessage)) {
            return Result.fail(ResultCode.FAIL.getCode(), "留言失败");
        }
        return Result.success("留言成功");
    }

    @Override
    public Result list(Long testPlanId) {
        if (Objects.isNull(testPlanId)) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LambdaQueryWrapper<TestPlanMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TestPlanMessage::getTestPlanId, testPlanId)
                .orderBy(true, false, TestPlanMessage::getCreateTime)
                .eq(TestPlanMessage::getDelFlag, NOT_DELETE);
        List<TestPlanMessage> testPlanMessageList = this.list(queryWrapper);

        if (testPlanMessageList == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "暂无数据");
        }

        List<TestPlanMessageDTO> testPlanMessageDTOS = testPlanMessageList.stream().map(item -> {
            TestPlanMessageDTO testPlanMessageDTO = new TestPlanMessageDTO();
            BeanUtils.copyProperties(item, testPlanMessageDTO);
            Long createBy = item.getCreateBy();
            User user = userMapper.selectById(createBy);
            testPlanMessageDTO.setCreateAvatar(user.getAvatar());
            testPlanMessageDTO.setCreateName(user.getNickName());
            return testPlanMessageDTO;
        }).toList();

        return Result.success(testPlanMessageDTOS);
    }
}
