package com.spms.service;

import com.spms.dto.Result;
import com.spms.entity.RatedTimeCost;

public interface RatedTimeCostService{
    Result updateCost(RatedTimeCost ratedTimeCost);

    Result list(RatedTimeCost ratedTimeCost, Integer page, Integer size);

    Result queryById(Long ratedTimeCostId);

    Result delete(Long[] ids);
}
