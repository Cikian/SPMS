package com.spms.service;

import com.spms.dto.RatedTimeCostDTO;
import com.spms.dto.Result;
import com.spms.entity.RatedTimeCost;

public interface RatedTimeCostService{
    Result updateCost(RatedTimeCost ratedTimeCost);

    Result list(RatedTimeCostDTO ratedTimeCost, Integer page, Integer size);

    Result queryById(Long ratedTimeCostId);

    Result delete(Long[] ids);
}
