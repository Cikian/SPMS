package com.spms.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long userId;
    private String userName;
    private String nickName;
    private Boolean status;
    private String email;
    private String phoneNumber;
    private String gender;
    private String avatar;
    private LocalDateTime createTime;
}
