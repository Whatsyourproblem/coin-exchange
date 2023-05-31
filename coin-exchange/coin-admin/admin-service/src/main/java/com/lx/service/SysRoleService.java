package com.lx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lx.domain.SysRole;

public interface SysRoleService extends IService<SysRole>{


    /*
    *  判断一个用户是否为超级管理员
    * */
    boolean isSuperAdmin(Long userId);

    /*
     *  根据使用角色的名称模糊分页角色查询
     * */
    Page<SysRole> findByPage(Page<SysRole> page, String name);
}
