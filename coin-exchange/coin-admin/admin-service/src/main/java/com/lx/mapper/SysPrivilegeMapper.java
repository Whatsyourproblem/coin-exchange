package com.lx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lx.domain.SysPrivilege;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

public interface SysPrivilegeMapper extends BaseMapper<SysPrivilege> {
    /**
     * 根据角色id查询权限
     * @param roleId
     **/
    Set<Long> getPrivilegesByRoleId(@Param("roleId") long roleId);

}