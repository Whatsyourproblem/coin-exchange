package com.lx.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.SysUser;
import com.lx.model.R;
import com.lx.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;

/**
 * 员工管理
 */
@Api(tags = "员工管理")
@RestController
@RequestMapping("/users")
public class    SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping
    @ApiOperation(value = "分页条件查询员工")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current" ,value = "当前页") ,
            @ApiImplicitParam(name = "size" ,value = "每页显示的条数") ,
            @ApiImplicitParam(name = "mobile" ,value = "员工的手机号码") ,
            @ApiImplicitParam(name = "fullname" ,value = "员工的全名称") ,
    })
    @PreAuthorize("hasAuthority('sys_user_query')")
    public R<Page<SysUser>> findByPage(@ApiIgnore Page<SysUser> page, String mobile, String fullname) {
        page.addOrder(OrderItem.descs("last_update_time"));
        Page<SysUser> usersPage = sysUserService.findByPage(page, mobile, fullname);
        return R.ok(usersPage);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增员工")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysUser"  ,value = "sysUser 的json数据")
    })
    @PreAuthorize("hasAuthority('sys_user_create')")
    public R addUser(@RequestBody SysUser sysUser) {
        boolean success = sysUserService.addUser(sysUser);
        return success ? R.ok() : R.fail("新增失败");
    }

    @PatchMapping("/update")
    @ApiOperation(value = "修改员工")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysUser"  ,value = "sysUser 的json数据")
    })
    @PreAuthorize("hasAuthority('sys_user_update')")
    public R updateUser(@RequestBody SysUser sysUser) {
        boolean success = sysUserService.updateUser(sysUser);
        return success ? R.ok() : R.fail("修改失败");
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除用户")
    @PreAuthorize("hasAuthority('sys_user_delete')")
    public R deleteUser(@RequestBody Long ids[]) {
        boolean success = sysUserService.removeByIds(Arrays.asList(ids));
        return success ? R.ok() : R.fail("删除失败");
    }


}
