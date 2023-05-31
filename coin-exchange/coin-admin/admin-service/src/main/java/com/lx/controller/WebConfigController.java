package com.lx.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.WebConfig;
import com.lx.model.R;
import com.lx.service.WebConfigService;
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

/**
 * 资源配置
 */
@Api(value = "资源配置")
@RestController
@RequestMapping("/webConfigs")
public class WebConfigController {

    @Autowired
    private WebConfigService webConfigService;

    @GetMapping
    @ApiOperation(value = "分页查询我们的资源配置(webConfig)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "webConfig的名称"),
            @ApiImplicitParam(name = "type", value = "webConfig的类型"), // nteger current ,Integer siz
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页的大小"),
    })
    @PreAuthorize("hasAuthority('web_config_query')")
    public R<Page<WebConfig>> findByPage(@ApiIgnore Page<WebConfig> page, String name, String type) {
        page.addOrder(OrderItem.desc("sort"));
        Page<WebConfig> webConfigPage = webConfigService.findByPage(page, name, type);
        return R.ok(webConfigPage);
    }

    @PostMapping
    @ApiOperation(value = "新增一个WebConfig")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "webConfig", value = "webConfig 的json数据")
    })
    @PreAuthorize("hasAuthority('web_config_create')")
    public R add(@RequestBody @Validated WebConfig webConfig) {
        boolean success = webConfigService.save(webConfig);
        return success ? R.ok() : R.fail("新增失败");
    }

    @PatchMapping
    @ApiOperation(value = "修改一个WebConfig")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "webConfig", value = "webConfig 的json数据")
    })
    @PreAuthorize("hasAuthority('web_config_update')")
    public R update(@RequestBody @Validated WebConfig webConfig) {
        boolean success = webConfigService.updateById(webConfig);
        return success ? R.ok() : R.fail("修改失败");
    }


    @PostMapping("/delete")
    @ApiOperation(value = "删除WebConfig")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "要删除的数据的ids")
    })
    @PreAuthorize("hasAuthority('web_config_delete')")
    public R delete(@RequestBody String[] ids) {
        if (ids == null || ids.length == 0) {
            return R.fail("删除时需要给删除数据的Id");
        }
        boolean success = webConfigService.removeByIds(Arrays.asList(ids));
        return success ? R.ok() : R.fail("删除失败");
    }

    @GetMapping("/banners")
    @ApiOperation(value = "获取我们的pc端的banner图")
    public R<List<WebConfig>> banners(){
        List<WebConfig> banners = webConfigService.getPcBanner();
        return R.ok(banners);
    }
}
