package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spms.entity.ProAnnouncement;
import com.spms.mapper.ProAnnouncementMapper;
import com.spms.service.ProAnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Title: ProAnnouncementServiceimpl
 * @Author Cikian
 * @Package com.spms.service.impl
 * @Date 2024/5/7 上午12:12
 * @description: SPMS:
 */

@Service
public class ProAnnouncementServiceImpl implements ProAnnouncementService {
    @Autowired
    private ProAnnouncementMapper proAnnouncementMapper;

    @Override
    public Boolean addProAnnouncement(ProAnnouncement proAnnouncement) {
        int i = proAnnouncementMapper.insert(proAnnouncement);
        return i > 0;
    }

    @Override
    public ProAnnouncement selectById(Long id) {
        LambdaQueryWrapper<ProAnnouncement> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ProAnnouncement::getProId, id);
        ProAnnouncement proAnnouncement = proAnnouncementMapper.selectOne(lqw);
        return proAnnouncement;
    }

    @Transactional
    @Override
    public Boolean insert(ProAnnouncement proAnnouncement) {
        QueryWrapper<ProAnnouncement> qw = new QueryWrapper<>();
        qw.eq("pro_id", proAnnouncement.getProId());
        int delete = proAnnouncementMapper.delete(qw);
        int i = proAnnouncementMapper.insert(proAnnouncement);
        return i > 0;
    }
}
