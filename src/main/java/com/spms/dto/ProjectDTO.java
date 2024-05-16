package com.spms.dto;

import com.spms.entity.Project;
import lombok.Data;

@Data
public class ProjectDTO extends Project {
    private Boolean isReviewer;
}
