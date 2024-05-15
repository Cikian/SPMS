package com.spms.service;

import com.spms.dto.Result;

public interface RecentVisitService {
    Result recordVisit(Long id, Integer type);

    Result getRecentVisits();

    Result getRecentVisitsPro();
}
