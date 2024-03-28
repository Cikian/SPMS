package com.spms.dto;

import lombok.Data;

@Data
public class EmailVerifyDTO {
    private String email;
    private String code;
}
