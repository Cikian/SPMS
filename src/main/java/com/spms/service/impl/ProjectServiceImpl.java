package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.format.DataFormatMatcher;
import com.spms.dto.*;
import com.spms.entity.*;
import com.spms.enums.DeviceUsage;
import com.spms.enums.ResourceType;
import com.spms.enums.ResultCode;
import com.spms.mapper.*;
import com.spms.security.LoginUser;
import com.spms.service.DemandService;
import com.spms.service.NotificationService;
import com.spms.service.ProjectService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Title: ProjectServiceImpl
 * @Author Cikian
 * @Package com.spms.service.impl
 * @Date 2024/4/8 下午1:54
 * @description: SPMS: 项目业务层实现类
 */

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {
    @Autowired
    ProjectMapper projectMapper;
    @Autowired
    RatedTimeCostMapper ratedTimeCostMapper;
    @Autowired
    ProjectResourceMapper projectResourceMapper;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private DemandMapper demandMapper;
    // @Autowired
    // private DemandService demandService;

    @Transactional
    @Override
    public boolean addPro(AddProjectDTO addProjectDTO) {

        long days = Duration.between(addProjectDTO.getExpectedStartTime(), addProjectDTO.getExpectedEndTime()).toDays();

        Project project = new Project();
        project.setProStatus(-1);
        project.setProName(addProjectDTO.getProName());
        project.setProDesc(addProjectDTO.getProDesc());
        project.setProFlag(addProjectDTO.getProFlag());
        project.setProType(addProjectDTO.getProType());
        project.setProCustomer(addProjectDTO.getProCustomer());
        project.setExpectedStartTime(addProjectDTO.getExpectedStartTime());
        project.setExpectedEndTime(addProjectDTO.getExpectedEndTime());
        projectMapper.insert(project);
        Long proId = project.getProId();

        Long[] membersIds = addProjectDTO.getProMembersIds();
        for (Long memberId : membersIds) {
            ProjectResource pr = new ProjectResource();
            pr.setProjectId(proId);
            pr.setResourceId(memberId);
            pr.setResourceType(ResourceType.EMPLOYEE.getCode());
            pr.setEstimateStartTime(addProjectDTO.getExpectedStartTime());
            pr.setEstimateEndTime(addProjectDTO.getExpectedEndTime());
            pr.setUseType(1);
            pr.setActualCost(BigDecimal.ZERO);
            LambdaQueryWrapper<RatedTimeCost> lqw = new LambdaQueryWrapper<>();
            lqw.eq(RatedTimeCost::getResourceId, memberId);

            BigDecimal dailyCost = ratedTimeCostMapper.selectOne(lqw).getDailyCost();
            BigDecimal estimateCost = BigDecimal.valueOf(days).multiply(dailyCost);
            System.out.println("预计成本：" + dailyCost);
            pr.setEstimateCost(estimateCost);
            projectResourceMapper.insert(pr);
        }
        return true;
    }

    @Override
    public List<Project> getProjectByStatus(Integer status) {
        LambdaQueryWrapper<Project> projectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        projectLambdaQueryWrapper.eq(!Objects.isNull(status), Project::getProFlag, status);
        projectLambdaQueryWrapper.ne(Project::getProStatus, -1);
        projectLambdaQueryWrapper.ne(Project::getProStatus, -2);
        return projectMapper.selectList(projectLambdaQueryWrapper);
    }

    @Override
    public ProjectDTO getProById(Long id) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        Project project = projectMapper.selectById(id);

        ProjectDTO projectDTO = new ProjectDTO();
        BeanUtils.copyProperties(project, projectDTO);
        projectDTO.setIsReviewer(userId.equals(project.getCreateBy()));

        return projectDTO;
    }

    @Override
    public Result addMember(AddProResourceDTO addProResourceDTO) {
        Project project = projectMapper.selectById(addProResourceDTO.getProId());
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        if (!userId.equals(project.getCreateBy())) {
            return Result.fail(ResultCode.FAIL.getCode(), "您的权限不足");
        }
        // 计算员工预计成本
        LambdaQueryWrapper<RatedTimeCost> lqw = new LambdaQueryWrapper<>();
        lqw.eq(RatedTimeCost::getResourceId, addProResourceDTO.getMemberId());
        BigDecimal dailyCost = ratedTimeCostMapper.selectOne(lqw).getDailyCost();

        if (dailyCost == null || dailyCost.equals(BigDecimal.ZERO)) {
            return Result.fail(ResultCode.FAIL.getCode(), "请先为员工设置工时费用");
        }
        long days = Duration.between(addProResourceDTO.getEstimateStartTime(), addProResourceDTO.getEstimateEndTime()).toDays();
        BigDecimal estimateCost = BigDecimal.valueOf(days).multiply(dailyCost);

        ProjectResource projectResource = new ProjectResource();
        projectResource.setProjectId(addProResourceDTO.getProId());
        projectResource.setResourceId(addProResourceDTO.getMemberId());
        projectResource.setResourceType(ResourceType.EMPLOYEE.getCode());
        projectResource.setEstimateStartTime(addProResourceDTO.getEstimateStartTime());
        projectResource.setEstimateEndTime(addProResourceDTO.getEstimateEndTime());
        projectResource.setUseType(1);
        projectResource.setActualCost(BigDecimal.ZERO);
        projectResource.setEstimateCost(estimateCost);
        projectResourceMapper.insert(projectResource);
        // 发送通知
        notificationService.addNotification(addProResourceDTO.getMemberId(),
                "您已被添加到项目：" + project.getProName(), "加入项目");
        return Result.success();
    }

    @Override
    public Result deleteMember(DeleteProResourceDTO deleteProResourceDTO) {
        ProjectResource projectResource = projectResourceMapper.selectById(deleteProResourceDTO.getId());
        Project project = projectMapper.selectById(projectResource.getProjectId());
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        if (!userId.equals(project.getCreateBy())) {
            return Result.fail(ResultCode.FAIL.getCode(), "您的权限不足");
        }
        // 计算实际成本
        long days = Duration.between(deleteProResourceDTO.getActualStartTime(), deleteProResourceDTO.getActualEndTime()).toDays();
        LambdaQueryWrapper<RatedTimeCost> lqw = new LambdaQueryWrapper<>();
        lqw.eq(RatedTimeCost::getResourceId, projectResource.getResourceId());
        BigDecimal dailyCost = ratedTimeCostMapper.selectOne(lqw).getDailyCost();
        BigDecimal actualCost = BigDecimal.valueOf(days).multiply(dailyCost);
        projectResource.setActualStartTime(deleteProResourceDTO.getActualStartTime());
        projectResource.setActualEndTime(deleteProResourceDTO.getActualEndTime());
        projectResource.setActualCost(actualCost);
        projectResourceMapper.updateById(projectResource);
        return Result.success();
    }

    @Override
    @Transactional
    public Result addDevice(AddProResourceDTO addProResourceDTO) {
        Project project = projectMapper.selectById(addProResourceDTO.getProId());
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        if (!userId.equals(project.getCreateBy())) {
            return Result.fail(ResultCode.FAIL.getCode(), "您的权限不足");
        }

        LambdaQueryWrapper<RatedTimeCost> lqw = new LambdaQueryWrapper<>();
        lqw.eq(RatedTimeCost::getResourceId, addProResourceDTO.getMemberId());
        BigDecimal dailyCost = ratedTimeCostMapper.selectOne(lqw).getDailyCost();
        if (dailyCost == null || dailyCost.equals(BigDecimal.ZERO)) {
            return Result.fail(ResultCode.FAIL.getCode(), "请先为设备设置工时费用");
        }

        ProjectResource projectResource = new ProjectResource();
        projectResource.setProjectId(addProResourceDTO.getProId());
        projectResource.setResourceId(addProResourceDTO.getMemberId());
        projectResource.setResourceType(ResourceType.DEVICE.getCode());
        projectResource.setEstimateStartTime(addProResourceDTO.getEstimateStartTime());
        projectResource.setEstimateEndTime(addProResourceDTO.getEstimateEndTime());
        projectResource.setUseType(1);
        projectResource.setActualCost(BigDecimal.ZERO);

        long days = Duration.between(addProResourceDTO.getEstimateStartTime(), addProResourceDTO.getEstimateEndTime()).toDays();
        BigDecimal estimateCost = BigDecimal.valueOf(days).multiply(dailyCost);
        projectResource.setEstimateCost(estimateCost);

        if (projectResourceMapper.insert(projectResource) < 0) {
            return Result.fail(ResultCode.FAIL.getCode(), "添加失败");
        }

        LambdaUpdateWrapper<Device> deviceLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        deviceLambdaUpdateWrapper.set(Device::getDeviceUsage, DeviceUsage.OCCUPIED.getCode())
                .set(Device::getUpdateTime, LocalDateTime.now())
                .set(Device::getUpdateBy, userId)
                .eq(Device::getDevId, addProResourceDTO.getMemberId());
        if (deviceMapper.update(deviceLambdaUpdateWrapper) < 0) {
            return Result.fail(ResultCode.FAIL.getCode(), "添加失败");
        }
        return Result.success();
    }

    @Override
    public Result deleteDevice(DeleteProResourceDTO deleteProResourceDTO) {
        ProjectResource projectResource = projectResourceMapper.selectById(deleteProResourceDTO.getId());

        Project project = projectMapper.selectById(projectResource.getProjectId());
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        if (!userId.equals(project.getCreateBy())) {
            return Result.fail(ResultCode.FAIL.getCode(), "您的权限不足");
        }

        long days = Duration.between(deleteProResourceDTO.getActualStartTime(), deleteProResourceDTO.getActualEndTime()).toDays();
        LambdaQueryWrapper<RatedTimeCost> lqw = new LambdaQueryWrapper<>();
        lqw.eq(RatedTimeCost::getResourceId, projectResource.getResourceId());
        BigDecimal dailyCost = ratedTimeCostMapper.selectOne(lqw).getDailyCost();
        BigDecimal actualCost = BigDecimal.valueOf(days).multiply(dailyCost);

        projectResource.setActualStartTime(deleteProResourceDTO.getActualStartTime());
        projectResource.setActualEndTime(deleteProResourceDTO.getActualEndTime());
        projectResource.setActualCost(actualCost);
        if (projectResourceMapper.updateById(projectResource) < 0) {
            return Result.fail(ResultCode.FAIL.getCode(), "释放失败");
        }

        LambdaUpdateWrapper<Device> deviceLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        deviceLambdaUpdateWrapper.set(Device::getDeviceUsage, DeviceUsage.FREE.getCode())
                .set(Device::getUpdateTime, LocalDateTime.now())
                .set(Device::getUpdateBy, userId)
                .eq(Device::getDevId, projectResource.getResourceId());
        if (deviceMapper.update(deviceLambdaUpdateWrapper) < 0) {
            return Result.fail(ResultCode.FAIL.getCode(), "释放失败");
        }

        return Result.success();
    }

    @Override
    public Boolean changeStatus(Long id, Integer status) {
        Project project = projectMapper.selectById(id);
        project.setProStatus(status);

        return projectMapper.updateById(project) > 0;
    }

    @Transactional
    @Override
    public Boolean deleteByProId(Long id) {
        LambdaQueryWrapper<ProjectResource> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ProjectResource::getProjectId, id);
        projectResourceMapper.delete(lqw);

        LambdaQueryWrapper<Project> projectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        projectLambdaQueryWrapper.eq(Project::getProId, id);
        projectLambdaQueryWrapper.eq(Project::getProStatus, -1).or().eq(Project::getProStatus, -2);
        return projectMapper.delete(projectLambdaQueryWrapper) > 0;
    }

    @Override
    public Boolean judgeIsProHeader(Long proId) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();

        LambdaQueryWrapper<Project> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Project::getProId, proId);
        Project project = projectMapper.selectOne(lqw);
        return project.getCreateBy().equals(userId);
    }

    @Override
    public List<Project> getAllPro() {
        LambdaQueryWrapper<Project> lqw = new LambdaQueryWrapper<>();
        lqw.ne(Project::getProStatus, -1);
        lqw.ne(Project::getProStatus, -2);
        lqw.orderByDesc(Project::getCreateTime);

        return projectMapper.selectList(lqw);
    }

    @Override
    public List<Project> getNeedCompletePro() {
        LambdaQueryWrapper<Project> lqw = new LambdaQueryWrapper<>();
        lqw.ne(Project::getProStatus, 3);
        lqw.ne(Project::getProStatus, -1);
        lqw.ne(Project::getProStatus, -2);
        lqw.orderByDesc(Project::getCreateTime);

        return projectMapper.selectList(lqw);
    }

    @Override
    public List<Project> myPro() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();

        LambdaQueryWrapper<ProjectResource> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ProjectResource::getResourceId, userId);
        lqw.eq(ProjectResource::getResourceType, ResourceType.EMPLOYEE.getCode());

        Set<Long> proIds = projectResourceMapper.selectList(lqw).stream().map(ProjectResource::getProjectId).collect(Collectors.toSet());
        LambdaQueryWrapper<Project> projectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        projectLambdaQueryWrapper.ne(Project::getProStatus, -1);
        projectLambdaQueryWrapper.ne(Project::getProStatus, -2);
        projectLambdaQueryWrapper.orderByDesc(Project::getCreateTime);
        projectLambdaQueryWrapper.in(Project::getProId, proIds);


        return projectMapper.selectList(projectLambdaQueryWrapper);
    }

    @Override
    public List<Project> mySubmit() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        LambdaQueryWrapper<Project> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Project::getCreateBy, userId);


        return projectMapper.selectList(lqw);
    }

    @Override
    public List<Project> getAudit() {
        LambdaQueryWrapper<Project> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Project::getProStatus, -1);
        lqw.orderByDesc(Project::getCreateTime);

        return projectMapper.selectList(lqw);
    }

    @Override
    public List<Project> searchPro(String keyword) {
        LambdaQueryWrapper<Project> lqw = new LambdaQueryWrapper<>();
        lqw.like(Project::getProName, keyword).or().like(Project::getProFlag, keyword);
        lqw.ne(Project::getProStatus, -1);
        lqw.ne(Project::getProStatus, -2);
        lqw.orderByDesc(Project::getCreateTime);

        return projectMapper.selectList(lqw);
    }


    @Override
    public List<List> getGantt(Long proId) {
        Project project = projectMapper.selectById(proId);

        LambdaQueryWrapper<Demand> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Demand::getProId, proId);
        lqw.isNotNull(Demand::getStartTime);
        lqw.isNotNull(Demand::getEndTime);
        lqw.orderByAsc(Demand::getStartTime);
        List<Demand> demands = demandMapper.selectList(lqw);

        List<Demand> demandsByLevel = processDemands(demands);

        List<List> ganttRowList = new ArrayList<>();
        for (Demand demand : demands) {
            GanttRowStyleDTO grs = new GanttRowStyleDTO();
            if (demand.getDemandStatus() == 2){
                grs.setBackground("#eeeeee");
            } else {
                switch (demand.getWorkItemType()) {
                    case 0:
                        grs.setBackground("#ff877b");
                        break;
                    case 1:
                        grs.setBackground("#9191f9");
                        break;
                    case 2:
                        grs.setBackground("#30d1fc");
                        break;
                    case 3:
                        grs.setBackground("#73d897");
                        break;
                    default:
                        grs.setBackground("#02fa5b");
                        break;
                }
            }

            grs.setColor("black");
            grs.setBorderRadius("5px");
            grs.setFontSize("12px");
            grs.setHeight("20px");

            GanttRowConfigDTO grc = new GanttRowConfigDTO();
            grc.setId(demand.getDemandId().toString());
            grc.setLabel(project.getProFlag() + "-" + demand.getDemandNo());
            grc.setStyle(grs);
            grc.setImmobile(true);
            grc.setHasHandles(false);

            GanttRowDTO gr = new GanttRowDTO();
            LocalDateTime startTime = demand.getStartTime();
            LocalDateTime endTime = demand.getEndTime();
            // 转换yyyy-MM-dd HH:mm格式
            String start = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            String end = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            gr.setMyBeginDate(start);
            gr.setMyEndDate(end);
            gr.setGanttBarConfig(grc);

            List<GanttRowDTO> dto = new ArrayList<>();
            dto.add(gr);
            ganttRowList.add(dto);
        }

        return ganttRowList;
    }

    @Override
    public Map<String, String> getTime(Long proId) {
        Project project = projectMapper.selectById(proId);
        Map<String, String> map = new HashMap<>();
        if (project != null){
            map.put("start", project.getExpectedStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            map.put("end", project.getExpectedEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }
        return map;
    }

    @Override
    public Project getProByDemandId(Long demandId) {
        Demand demand = demandMapper.selectById(demandId);
        return projectMapper.selectById(demand.getProId());
    }

    public List<Demand> processDemands(List<Demand> demands) {
        // 分离出demands中元素的不同level的元素

        List<Demand> level0Demands = new ArrayList<>();
        List<Demand> level1Demands = new ArrayList<>();
        List<Demand> level2Demands = new ArrayList<>();
        List<Demand> level3Demands = new ArrayList<>();
        for (Demand demand : demands) {
            if (demand.getLevel() == 0) {
                level0Demands.add(demand);
            } else if (demand.getLevel() == 1) {
                level1Demands.add(demand);
            } else if (demand.getLevel() == 2) {
                level2Demands.add(demand);
            } else if (demand.getLevel() == 3) {
                level3Demands.add(demand);
            }
        }
        findParent(level2Demands, level3Demands);
        findParent(level1Demands, level2Demands);
        findParent(level0Demands, level1Demands);
        return level0Demands;
    }

    public void findParent(List<Demand> fatherDemands, List<Demand> childDemands) {
        for (Demand fatherDemand : fatherDemands) {
            Long fatherId = fatherDemand.getDemandId();
            List<Demand> children = new ArrayList<>();
            for (Demand childDemand : childDemands) {
                if (Objects.equals(childDemand.getFatherDemandId(), fatherId)) {
                    children.add(childDemand);
                }
            }
            fatherDemand.setChildren(children);
        }
    }


}

