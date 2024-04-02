package com.spms.dto;

import lombok.Data;

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
}
