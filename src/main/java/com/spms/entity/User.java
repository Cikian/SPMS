package com.spms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    //    用户Id
    @TableId(type = IdType.ASSIGN_ID)
    private Long userId;

    //    用户名
    private String userName;

    //    用户昵称
    private String nickName;

    //    密码
    private String password;

    //    账号状态
    private Boolean status;

    //    邮箱
    private String email;

    //    手机号码
    private String phoneNumber;

    //    性别
    private String gender;

    //    头像
    private String avatar;

    //    创建人
    private Long createBy;

    //    创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    //    更新人
    private Long updateBy;

    //    更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    //    第一次登录
    private Boolean isFirstLogin;

    //    删除标记
    private Boolean delFlag;
}
