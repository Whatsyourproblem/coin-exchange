package com.lx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lx.domain.SysUser;

public interface SysUserService extends IService<SysUser>{


    /**
     * 分页条件查询员工
     * @param page 分页数据
     * @param mobile 手机号
     * @param fullname 员工全称
     **/
    Page<SysUser> findByPage(Page<SysUser> page, String mobile, String fullname);

    /**
     * 新增员工
     * @param sysUser
     **/
    boolean addUser(SysUser sysUser);

    /**
     * 修改员工
     * @param sysUser
     **/
    boolean updateUser(SysUser sysUser);


}
