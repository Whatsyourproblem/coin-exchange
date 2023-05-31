package com.lx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lx.domain.SysMenu;

import java.util.List;


public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /*
    *  通过用户的id 查询用户的菜单数据
    * */
    List<SysMenu> selectMenusByUserId(Long userId);
}