package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.QualityTargetRequirementDTO;
import com.spms.dto.Result;
import com.spms.entity.Demand;
import com.spms.entity.QualityTarget;
import com.spms.entity.QualityTargetRequirement;
import com.spms.enums.ResultCode;
import com.spms.mapper.DemandMapper;
import com.spms.mapper.QualityTargetMapper;
import com.spms.mapper.QualityTargetRequirementMapper;
import com.spms.service.QualityTargetRequirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.spms.constants.SystemConstants.DELETE;
import static com.spms.constants.SystemConstants.NOT_DELETE;

@Service
public class QualityTargetRequirementServiceImpl extends ServiceImpl<QualityTargetRequirementMapper, QualityTargetRequirement> implements QualityTargetRequirementService {

    @Autowired
    private QualityTargetRequirementMapper qualityTargetRequirementMapper;

    @Autowired
    private QualityTargetMapper qualityTargetMapper;

    @Autowired
    private DemandMapper demandMapper;

    @Override
    public Result add(QualityTargetRequirement qualityTargetRequirement) {
        LambdaQueryWrapper<QualityTargetRequirement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QualityTargetRequirement::getQualityTargetId, qualityTargetRequirement.getQualityTargetId())
                .eq(QualityTargetRequirement::getDemandId, qualityTargetRequirement.getDemandId());
        QualityTargetRequirement one = qualityTargetRequirementMapper.selectOne(wrapper);
        if (one != null) {
            if (one.getDelFlag().equals(NOT_DELETE)) {
                return Result.fail(ResultCode.FAIL.getCode(), "该需求已存在该质量目标");
            }
            LambdaUpdateWrapper<QualityTargetRequirement> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(QualityTargetRequirement::getQualityTargetId, qualityTargetRequirement.getQualityTargetId())
                    .eq(QualityTargetRequirement::getDemandId, qualityTargetRequirement.getDemandId())
                    .set(QualityTargetRequirement::getDelFlag, NOT_DELETE);
            qualityTargetRequirementMapper.update(null, updateWrapper);
            return Result.success("关联成功");
        }
        qualityTargetRequirementMapper.insert(qualityTargetRequirement);
        return Result.success("关联成功");
    }

    @Override
    public Result delete(Long demandId, Long targetId) {
        if (demandId == null || targetId == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LambdaQueryWrapper<QualityTargetRequirement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QualityTargetRequirement::getQualityTargetId, targetId)
                .eq(QualityTargetRequirement::getDemandId, demandId);
        QualityTargetRequirement qualityTargetRequirement = qualityTargetRequirementMapper.selectOne(wrapper);
        if (qualityTargetRequirement == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "该需求不存在该质量目标");
        }

        LambdaUpdateWrapper<QualityTargetRequirement> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(QualityTargetRequirement::getQualityTargetId, targetId)
                .eq(QualityTargetRequirement::getDemandId, demandId)
                .set(QualityTargetRequirement::getDelFlag, DELETE);
        qualityTargetRequirementMapper.update(null, updateWrapper);
        return Result.success("解除关联成功");
    }

    @Override
    public Result byPro(Long proId) {
        List<QualityTargetRequirement> list = qualityTargetRequirementMapper.selectByProId(proId);

        List<QualityTargetRequirementDTO> qualityTargetRequirementDTOS = list.stream().map(item -> {
            QualityTargetRequirementDTO qualityTargetRequirementDTO = new QualityTargetRequirementDTO();
            QualityTarget qualityTarget = qualityTargetMapper.selectById(item.getQualityTargetId());
            Demand demand = demandMapper.selectById(item.getDemandId());
            qualityTargetRequirementDTO.setQualityTargetId(qualityTarget.getQualityTargetId());
            qualityTargetRequirementDTO.setTargetName(qualityTarget.getTargetName());
            qualityTargetRequirementDTO.setDemandId(demand.getDemandId());
            qualityTargetRequirementDTO.setDemandName(demand.getTitle());
            return qualityTargetRequirementDTO;
        }).toList();

        return Result.success(qualityTargetRequirementDTOS);
    }
}
