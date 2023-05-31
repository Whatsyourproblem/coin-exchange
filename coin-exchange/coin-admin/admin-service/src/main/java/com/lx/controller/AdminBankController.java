package com.lx.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.AdminBank;
import com.lx.dto.AdminBankDto;
import com.lx.feign.AdminBankServiceFeign;
import com.lx.model.R;
import com.lx.service.AdminBankService;
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
 * 公司银行卡的配置
 */
@Api(tags = "公司银行卡的配置")
@RestController
@RequestMapping("/adminBanks")
public class AdminBankController implements AdminBankServiceFeign {

    @Autowired
    private AdminBankService adminBankService;

    @GetMapping
    @ApiOperation(value = "条件查询公司银行卡")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bankCard", value = "公司的银行卡"),
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示的条数")
    })
    @PreAuthorize("hasAuthority('admin_bank_query')")
    public R<Page<AdminBank>> findByPage(@ApiIgnore Page<AdminBank> page , String bankCard){
        Page<AdminBank> adminBankPage = adminBankService.findByPage(page, bankCard) ;
        return R.ok(adminBankPage) ;
    }

    @PostMapping
    @ApiOperation(value = "新增一个银行卡")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "adminBank" ,value = "adminBank json数据")
    })
    @PreAuthorize("hasAuthority('admin_bank_create')")
    public R add(@RequestBody @Validated AdminBank adminBank){
        boolean success = adminBankService.save(adminBank);
        return success ? R.ok() : R.fail("新增失败");
    }


    @PatchMapping
    @ApiOperation(value = "修改一个银行卡")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "adminBank" ,value = "adminBank json数据")
    })
    @PreAuthorize("hasAuthority('admin_bank_update')")
    public R update(@RequestBody @Validated AdminBank adminBank){
        boolean success = adminBankService.updateById(adminBank);
        return success ? R.ok() : R.fail("修改失败");
    }


    @PostMapping("/adminUpdateBankStatus")
    @ApiOperation(value = "修改银行卡的状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bankId" ,value = "要修改的银行卡的ID"),
            @ApiImplicitParam(name = "status" ,value = "要修改为的状态")
    })
    @PreAuthorize("hasAuthority('admin_bank_update')")
    public R changeStatus(Long bankId ,Byte status){
        AdminBank adminBank = new AdminBank();
        adminBank.setId(bankId);
        adminBank.setStatus(status);
        boolean success = adminBankService.updateById(adminBank);
        return success ? R.ok() : R.fail("状态修改失败");
    }

    @PostMapping("/delete")
    @ApiOperation(value = "修改银行卡的状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bankIds" ,value = "要删除的银行卡的ID"),
    })
    @PreAuthorize("hasAuthority('admin_bank_delete')")
    public R delete(@RequestBody String[] ids){
        if (ids == null || ids.length == 0) {
            return R.fail("删除失败");
        }
        List<Long> collect = Arrays.stream(ids).map(Long::valueOf).collect(Collectors.toList());
        boolean success = adminBankService.removeByIds(collect);
        return success ? R.ok() : R.fail("删除失败");
    }

    /*
    *  adminBank的远程调用实现
    * */
    @Override
    public List<AdminBankDto> getAllAdminBanks() {
        List<AdminBankDto> adminBankDtos = adminBankService.getAllAdminBanks();
        return adminBankDtos;
    }
}
