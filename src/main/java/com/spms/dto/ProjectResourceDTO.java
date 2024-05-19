package com.spms.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectResourceDTO {
    private Long id;
    private Long resourceId;
    private String resourceName;
    private List<String> role;
    private LocalDateTime estimateStartTime;
    private LocalDateTime estimateEndTime;
}
