package com.spms.service.impl;

import com.spms.dto.RecentVisitDTO;
import com.spms.dto.Result;
import com.spms.entity.Demand;
import com.spms.entity.Project;
import com.spms.entity.TestPlan;
import com.spms.enums.RecentVisitType;
import com.spms.mapper.DemandMapper;
import com.spms.mapper.ProjectMapper;
import com.spms.mapper.TestPlanMapper;
import com.spms.security.LoginUser;
import com.spms.service.RecentVisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.spms.constants.RedisConstants.*;

@Service
public class RecentVisitServiceImpl implements RecentVisitService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private DemandMapper demandMapper;

    @Autowired
    private TestPlanMapper testPlanMapper;

    @Override
    public Result recordVisit(Long id, Integer type) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        String itemId = id + ":" + type;
        // 如果之前添加过则删除
        if (redisTemplate.opsForZSet().rank(RECENT_VISIT_PRO + userId, itemId) != null) {
            redisTemplate.opsForZSet().remove(RECENT_VISIT_PRO + userId, itemId);
        }
        if (redisTemplate.opsForZSet().rank(RECENT_VISIT_OTHER + userId, itemId) != null) {
            redisTemplate.opsForZSet().remove(RECENT_VISIT_OTHER + userId, itemId);
        }
        double score = System.currentTimeMillis();
        // 添加到redis
        if (RecentVisitType.PROJECT.getType().equals(type)) {
            redisTemplate.opsForZSet().add(RECENT_VISIT_PRO + userId, itemId, score);
        } else {
            redisTemplate.opsForZSet().add(RECENT_VISIT_OTHER + userId, itemId, score);
        }
        // 删除超出范围的数据，项目不超过5条，其他不超过15条
        redisTemplate.opsForZSet().removeRange(RECENT_VISIT_PRO + userId, 0, -6);
        redisTemplate.opsForZSet().removeRange(RECENT_VISIT_OTHER + userId, 0, -16);
        return Result.success();
    }

    @Override
    public Result getRecentVisits() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();

        Set<String> strings = redisTemplate.opsForZSet().reverseRange(RECENT_VISIT_OTHER + userId, 0, -1);

        if (strings == null) {
            return Result.success();
        }

        List<RecentVisitDTO> recentVisitDTOS = strings.stream().map(item -> {
            RecentVisitDTO recentVisitDTO = new RecentVisitDTO();
            String[] split = item.split(":");
            String id = split[0];
            Integer type = Integer.valueOf(split[1]);
            recentVisitDTO.setId(Long.valueOf(id));
            Double score = redisTemplate.opsForZSet().score(RECENT_VISIT_OTHER + userId, item);
            recentVisitDTO.setTime(score.longValue());

            if (RecentVisitType.DEMAND.getType().equals(type)) {
                Demand demand = demandMapper.selectById(Long.valueOf(id));
                recentVisitDTO.setId(demand.getDemandId());
                recentVisitDTO.setType(2);
                recentVisitDTO.setName(demand.getTitle());
                recentVisitDTO.setDemandType(demand.getWorkItemType());
            } else if (RecentVisitType.TEST_PLAN.getType().equals(type)) {
                TestPlan testPlan = testPlanMapper.selectById(Long.valueOf(id));
                recentVisitDTO.setId(testPlan.getTestPlanId());
                recentVisitDTO.setType(3);
                recentVisitDTO.setName(testPlan.getPlanName());
            }
            return recentVisitDTO;
        }).toList();

        return Result.success(recentVisitDTOS);
    }

    @Override
    public Result getRecentVisitsPro() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();

        Set<String> strings = redisTemplate.opsForZSet().reverseRange(RECENT_VISIT_PRO + userId, 0, -1);
        if (strings == null) {
            return Result.success();
        }

        List<RecentVisitDTO> recentVisitDTOS = strings.stream().map(item -> {
            RecentVisitDTO recentVisitDTO = new RecentVisitDTO();
            String[] split = item.split(":");
            String id = split[0];
            recentVisitDTO.setId(Long.valueOf(id));
            Project project = projectMapper.selectById(Long.valueOf(id));
            recentVisitDTO.setId(project.getProId());
            recentVisitDTO.setName(project.getProName());
            recentVisitDTO.setFlag(project.getProFlag());
            recentVisitDTO.setDesc(project.getProDesc());
            return recentVisitDTO;
        }).toList();

        return Result.success(recentVisitDTOS);
    }
}
