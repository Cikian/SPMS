package com.spms.service;

import com.spms.dto.EmailVerifyDTO;
import com.spms.dto.PasswordUpdateDTO;
import com.spms.dto.Result;
import com.spms.dto.UserDTO;
import com.spms.entity.User;

import java.util.List;

public interface UserService {

    Result login(User user);

    Result logout();

    Result add(User user);

    Result sendEmailCode(String email);

    Result verifyEmail(EmailVerifyDTO emailVerifyDTO);

    Result updatePassword(PasswordUpdateDTO passwordUpdateDTO);

    Result delete(Long[] ids);

    Result list(UserDTO userDTO, Integer page, Integer size);

    Result updateStatus(UserDTO userDTO);

    Result queryById(Long id);

    Result updateUserBaseInfo(User user);

    Result queryCurrentUser();

    Result queryCanAddToProject();

    Result queryProjectMembers(Long projectId, Integer type);

    Result queryCanAddToProjectMember(Long proId);
}
