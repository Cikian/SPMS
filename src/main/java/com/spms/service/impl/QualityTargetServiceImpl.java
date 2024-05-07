package com.spms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.Result;
import com.spms.entity.QualityTarget;
import com.spms.entity.QualityTargetRequirement;
import com.spms.enums.ResultCode;
import com.spms.mapper.QualityTargetMapper;
import com.spms.mapper.QualityTargetRequirementMapper;
import com.spms.service.QualityTargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.spms.constants.SystemConstants.DELETE;
import static com.spms.constants.SystemConstants.NOT_DELETE;

@Service
public class QualityTargetServiceImpl extends ServiceImpl<QualityTargetMapper, QualityTarget> implements QualityTargetService {

    @Autowired
    private QualityTargetRequirementMapper qualityTargetRequirementMapper;

    @Override
    public Result add(QualityTarget qualityTarget) {
        if (qualityTarget == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        if (StrUtil.isEmpty(qualityTarget.getTargetName())) {
            return Result.fail(ResultCode.FAIL.getCode(), "目标名称不能为空");
        }

        if (Objects.isNull(qualityTarget.getQualityAttribute())) {
            return Result.fail(ResultCode.FAIL.getCode(), "质量特性不能为空");
        }

        if (StrUtil.isEmpty(qualityTarget.getTargetValue())) {
            return Result.fail(ResultCode.FAIL.getCode(), "目标值不能为空");
        }

        if (Objects.isNull(qualityTarget.getPriority())) {
            return Result.fail(ResultCode.FAIL.getCode(), "目标优先级不能为空");
        }

        if (StrUtil.isEmpty(qualityTarget.getMetric())) {
            return Result.fail(ResultCode.FAIL.getCode(), "度量指标不能为空");
        }

        LambdaQueryWrapper<QualityTarget> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(QualityTarget::getTargetName, qualityTarget.getTargetName());
        QualityTarget target = this.getOne(queryWrapper);

        if (target != null) {
            return Result.fail(ResultCode.FAIL.getCode(), "目标名称已存在");
        }

        qualityTarget.setDelFlag(NOT_DELETE);
        boolean save = this.save(qualityTarget);

        return save ? Result.success("添加成功") : Result.fail(ResultCode.FAIL.getCode(), "添加失败");
    }

    @Override
    public Result delete(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LambdaQueryWrapper<QualityTargetRequirement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(QualityTargetRequirement::getDelFlag, NOT_DELETE)
                .in(QualityTargetRequirement::getQualityTargetId, ids);
        List<QualityTargetRequirement> list = qualityTargetRequirementMapper.selectList(queryWrapper);
        if (!list.isEmpty()) {
            return Result.fail(ResultCode.FAIL.getCode(), "数据已被关联，无法删除");
        }

        LambdaUpdateWrapper<QualityTarget> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(QualityTarget::getQualityTargetId, ids)
                .set(QualityTarget::getDelFlag, DELETE);
        if (!this.update(updateWrapper)) {
            return Result.fail(ResultCode.FAIL.getCode(), "删除失败");
        }

        return Result.success("删除成功");
    }

    @Override
    public Result list(QualityTarget qualityTarget, Integer page, Integer size) {
        Page<QualityTarget> qualityTargetPage = new Page<>(page, size);

        LocalDateTime minCreateDate = null;
        LocalDateTime maxCreateDate = null;
        if (qualityTarget.getCreateTime() != null) {
            minCreateDate = qualityTarget.getCreateTime().withHour(0).withMinute(0).withSecond(0).withNano(0);
            maxCreateDate = qualityTarget.getCreateTime().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        }

        LambdaQueryWrapper<QualityTarget> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .like(StrUtil.isNotEmpty(qualityTarget.getTargetName()), QualityTarget::getTargetName, qualityTarget.getTargetName())
                .eq(Objects.nonNull(qualityTarget.getQualityAttribute()), QualityTarget::getQualityAttribute, qualityTarget.getQualityAttribute())
                .eq(Objects.nonNull(qualityTarget.getPriority()), QualityTarget::getPriority, qualityTarget.getPriority())
                .between(Objects.nonNull(qualityTarget.getCreateTime()), QualityTarget::getCreateTime, minCreateDate, maxCreateDate)
                .eq(QualityTarget::getDelFlag, NOT_DELETE)
                .select(QualityTarget::getQualityTargetId, QualityTarget::getTargetName, QualityTarget::getQualityAttribute, QualityTarget::getTargetValue,
                        QualityTarget::getPriority, QualityTarget::getMetric, QualityTarget::getCreateTime);
        this.page(qualityTargetPage, queryWrapper);

        if (qualityTargetPage.getRecords().isEmpty()) {
            return Result.fail(ResultCode.FAIL.getCode(), "暂无数据");
        }
        return Result.success(qualityTargetPage);
    }
}
