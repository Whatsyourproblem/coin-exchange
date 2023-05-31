package com.mapper;

import com.domain.SysRolePrivilege;

public interface SysRolePrivilegeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SysRolePrivilege record);

    int insertSelective(SysRolePrivilege record);

    SysRolePrivilege selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysRolePrivilege record);

    int updateByPrimaryKey(SysRolePrivilege record);
}