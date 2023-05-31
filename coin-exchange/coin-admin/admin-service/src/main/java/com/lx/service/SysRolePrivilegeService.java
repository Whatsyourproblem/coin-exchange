package com.lx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lx.domain.SysMenu;
import com.lx.domain.SysRolePrivilege;
import com.lx.model.RolePrivilegesParam;

import java.util.List;

public interface SysRolePrivilegeService extends IService<SysRolePrivilege>{

    /**
     * 查询角色的权限
     * @param roleId
     **/
    List<SysMenu> findSysMenuAndPrivileges(long roleId);

    /**
     * 给角色授权
     * @param rolePrivilegesParam
     **/
    boolean grantPrivileges(RolePrivilegesParam rolePrivilegesParam);
}
