package com.example.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@TableName("sys_user")
public class SysUser extends BaseEntity {
    private String email;

    private String password;

    private String name;

    private String mobile;

    private String avatar;

    private Integer enabled;
}
