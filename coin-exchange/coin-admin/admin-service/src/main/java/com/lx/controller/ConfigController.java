package com.lx.controller;


import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.Config;
import com.lx.model.R;
import com.lx.service.ConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 后台参数配置
 */
@Api(tags = "后台参数配置")
@RestController
@RequestMapping("/configs")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @GetMapping
    @ApiOperation(value = "条件分页查询后台参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "后台参数类型"),
            @ApiImplicitParam(name = "code", value = "后台参数code"),
            @ApiImplicitParam(name = "name", value = "后台参数名称"),
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示的条数"),
    })
    @PreAuthorize("hasAuthority('config_query')")
    public R<Page<Config>> findByPage(@ApiIgnore Page<Config> page, String type, String code, String name) {
        page.addOrder(OrderItem.desc("created"));
        Page<Config> configPage = configService.findByPage(page, type, name, code);
        return R.ok(configPage);
    }

    @PostMapping
    @ApiOperation(value = "新增一个参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "config",value = "config 的json数据")
    })
    @PreAuthorize("hasAuthority('config_create')")
    public R add(@RequestBody @Validated Config config){
        boolean success = configService.save(config);
        return success ? R.ok() : R.fail("新增失败") ;
    }



    @PatchMapping
    @ApiOperation(value = "修改一个参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "config",value = "config 的json数据")
    })
    @PreAuthorize("hasAuthority('config_update')")
    public R update(@RequestBody @Validated  Config config){
        boolean success = configService.updateById(config);
        return success ? R.ok() : R.fail("修改失败") ;
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除一个参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids",value = "参数的IDS")
    })
    @PreAuthorize("hasAuthority('config_delete')")
    public R update(@RequestBody String[] ids){
        if (ids == null || ids.length == 0) {
            return R.fail("删除失败");
        }
        List<Long> list = Arrays.stream(ids).map(Long::valueOf).collect(Collectors.toList());
        boolean success = configService.removeByIds(list);
        return success ? R.ok() : R.fail("删除失败") ;
    }
}
