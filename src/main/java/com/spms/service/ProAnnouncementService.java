package com.spms.service;

import com.spms.entity.ProAnnouncement;

/**
 * @Title: ProAnnouncementService
 * @Author Cikian
 * @Package com.spms.service
 * @Date 2024/5/7 上午12:11
 * @description: SPMS: 项目公告
 */
public interface ProAnnouncementService {
    Boolean addProAnnouncement(ProAnnouncement proAnnouncement);

    ProAnnouncement selectById(Long id);

    Boolean insert(ProAnnouncement proAnnouncement);
}
