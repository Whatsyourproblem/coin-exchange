package com.lx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lx.domain.SysMenu;
import com.lx.domain.SysPrivilege;
import com.lx.domain.SysRolePrivilege;
import com.lx.model.RolePrivilegesParam;
import com.lx.service.SysMenuService;
import com.lx.service.SysPrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.mapper.SysRolePrivilegeMapper;
import com.lx.service.SysRolePrivilegeService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@SuppressWarnings("all")
@Service
public class SysRolePrivilegeServiceImpl extends ServiceImpl<SysRolePrivilegeMapper, SysRolePrivilege> implements SysRolePrivilegeService{

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysPrivilegeService sysPrivilegeService;

    @Autowired
    private SysRolePrivilegeService sysRolePrivilegeService;

    /**
     * <h2>查询角色的权限</h2>
     *
     * @param roleId
     **/
    @Override
    public List<SysMenu> findSysMenuAndPrivileges(long roleId) {
        //查询所有的菜单
        List<SysMenu> menus = sysMenuService.list();
        //在页面显示的是二级菜单，以及二级菜单包含的权限
        //一级菜单
        List<SysMenu> oneMenus = menus.stream()
                .filter(sysMenu -> sysMenu.getParentId() == null).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(oneMenus)) {
            return Collections.emptyList();
        }
        List<SysMenu> twoMenus = new ArrayList<>();
        oneMenus.forEach(menu -> {
            twoMenus.addAll(getChildMenus(menu.getId(), roleId, menus));
        });
        return twoMenus;
    }

    /**
     * <h2>给角色授权</h2>
     * @param rolePrivilegesParam
     **/
    @Transactional
    @Override
    public boolean grantPrivileges(RolePrivilegesParam rolePrivilegesParam) {
        Long roleId = rolePrivilegesParam.getRoleId();
        //删除该角色之前的权限数据
        boolean remove = sysRolePrivilegeService.remove(new LambdaQueryWrapper<SysRolePrivilege>().eq(SysRolePrivilege::getRoleId, roleId));
        //删除成功再新增数据
        List<Long> privilegeIds = rolePrivilegesParam.getPrivilegeIds();
        if (!CollectionUtils.isEmpty(privilegeIds)) {
            List<SysRolePrivilege> rolePrivileges = new ArrayList<>();
            privilegeIds.forEach(privilegeId -> {
                SysRolePrivilege sysRolePrivilege = new SysRolePrivilege();
                sysRolePrivilege.setRoleId(roleId);
                sysRolePrivilege.setPrivilegeId(privilegeId);
                rolePrivileges.add(sysRolePrivilege);
            });
            //批量保存
            return sysRolePrivilegeService.saveBatch(rolePrivileges);
        }
        return true;
    }

    /**
     * <h2>查找菜单的子菜单</h2>
     *
     * @param parentId
     * @param roleId
     * @param sources
     **/
    private List<SysMenu> getChildMenus(Long parentId, long roleId, List<SysMenu> sources) {
        List<SysMenu> children = new ArrayList<>();
        for (SysMenu menu : sources) {
            if (Objects.equals(parentId, menu.getParentId())) {
                children.add(menu);
                menu.setChilds(getChildMenus(menu.getId(), roleId, sources));
                //给菜单添加权限
                List<SysPrivilege> privileges = sysPrivilegeService.getAllSysPrivilegesByRoleId(menu.getId(), roleId);
                menu.setPrivileges(privileges);
            }
        }
        return children;
    }


}
