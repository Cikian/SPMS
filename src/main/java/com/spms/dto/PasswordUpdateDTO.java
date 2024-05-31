package com.spms.dto;

import lombok.Data;

@Data
public class PasswordUpdateDTO {
    private String email;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
