package com.example.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.entity.SysUser;
import org.apache.ibatis.annotations.Select;

public interface SysUserMapper extends BaseMapper<SysUser> {
    /**
     * 根据邮箱查询用户 (未删除的用户)
     * @param email
     * @return
     */
    @Select("SELECT * FROM sys_user WHERE email = #{email} AND deleted = 0")
    SysUser selectByEmail(String email);
}
