package com.mapper;

import com.domain.SysRolePrivilegeUser;

public interface SysRolePrivilegeUserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SysRolePrivilegeUser record);

    int insertSelective(SysRolePrivilegeUser record);

    SysRolePrivilegeUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysRolePrivilegeUser record);

    int updateByPrimaryKey(SysRolePrivilegeUser record);
}