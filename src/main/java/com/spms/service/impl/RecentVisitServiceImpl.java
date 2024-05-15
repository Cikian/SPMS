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

import static com.spms.constants.RedisConstants.RECENT_VISIT;

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
        String key = RECENT_VISIT + userId;
        String itemId = id + ":" + type;

        if (redisTemplate.opsForZSet().rank(key, itemId) != null) {
            redisTemplate.opsForZSet().remove(key, itemId);
        }

        double score = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(key, itemId, score);

        redisTemplate.opsForZSet().removeRange(key, 0, -16);
        return Result.success();
    }

    @Override
    public Result getRecentVisits() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();

        Set<String> strings = redisTemplate.opsForZSet().reverseRange(RECENT_VISIT + userId, 0, -1);
        if (strings == null) {
            return Result.success();
        }

        List<RecentVisitDTO> recentVisitDTOS = strings.stream().map(item -> {
            RecentVisitDTO recentVisitDTO = new RecentVisitDTO();
            String[] split = item.split(":");
            String id = split[0];
            Integer type = Integer.valueOf(split[1]);
            recentVisitDTO.setId(Long.valueOf(id));

            if (RecentVisitType.PROJECT.getType().equals(type)) {
                Project project = projectMapper.selectById(Long.valueOf(id));
                recentVisitDTO.setType(1);
                recentVisitDTO.setName(project.getProName());
                recentVisitDTO.setFlag(project.getProFlag());
            } else if (RecentVisitType.DEMAND.getType().equals(type)) {
                Demand demand = demandMapper.selectById(Long.valueOf(id));
                recentVisitDTO.setType(2);
                recentVisitDTO.setName(demand.getTitle());
            } else if (RecentVisitType.TEST_PLAN.getType().equals(type)) {
                TestPlan testPlan = testPlanMapper.selectById(Long.valueOf(id));
                recentVisitDTO.setType(3);
                recentVisitDTO.setName(testPlan.getPlanName());
            }
            return recentVisitDTO;
        }).toList();

        return Result.success(recentVisitDTOS);
    }
}
