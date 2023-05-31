package com.lx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lx.domain.SysPrivilege;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.mapper.SysPrivilegeMapper;
import com.lx.service.SysPrivilegeService;
import org.springframework.util.CollectionUtils;

@Service

public class SysPrivilegeServiceImpl extends ServiceImpl<SysPrivilegeMapper, SysPrivilege> implements SysPrivilegeService{

    @Autowired
    private SysPrivilegeMapper sysPrivilegeMapper;


    /**
     * <h2>获取菜单下的所有权限数据</h2>
     * @param menuId 菜单id
     * @param roleId 角色id
     **/
    @Override
    public List<SysPrivilege> getAllSysPrivilegesByRoleId(Long menuId, long roleId) {
        //查询该菜单下的所有权限
        List<SysPrivilege> privileges = list(new LambdaQueryWrapper<SysPrivilege>().eq(SysPrivilege::getMenuId, menuId));
        if (CollectionUtils.isEmpty(privileges)) {
            return Collections.emptyList();
        }
        // 判断用户是否包含这些权限
        Set<Long> privilegesOfRole = sysPrivilegeMapper.getPrivilegesByRoleId(roleId);
        if (!CollectionUtils.isEmpty(privilegesOfRole)) {
            for (SysPrivilege privilege : privileges) {
                if (privilegesOfRole.contains(privilege.getId())) {
                    privilege.setOwn(1);
                }
            }
        }
        return privileges;
    }

}
