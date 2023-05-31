package com.lx.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.UserWallet;
import com.lx.model.R;
import com.lx.service.UserWalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@Api(tags = "用户的提币地址")
@RequestMapping("/userWallets")
public class UserWalletController {

    @Autowired
    private UserWalletService userWalletService;

    @GetMapping
    @ApiOperation(value = "分页查询用户的提币地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户的id"),
            @ApiImplicitParam(name = "current",value = "当前页"),
            @ApiImplicitParam(name = "size",value = "每一页显示的条数"),
    })
    @PreAuthorize("hasAuthority('user_wallet_query')")
    public R<Page<UserWallet>> findByPage(@ApiIgnore Page<UserWallet> page, Long userId){
        page.addOrder(OrderItem.desc("last_update_time"));
        Page<UserWallet> userWalletPage = userWalletService.findByPage(page,userId);
        return R.ok(userWalletPage);
    }

    @GetMapping("/getCoinAddress/{coinId}")
    @ApiOperation("查询用户指定币种的提现地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "coinId", value = "币种Id"),
    })
    public R<List<UserWallet>> findUserWallets(@PathVariable Long coinId) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        List<UserWallet> userWallets = userWalletService.findUserWallets(userId, coinId);
        return R.ok(userWallets);
    }

    @PostMapping
    @ApiOperation("新增用户指定币种的提现地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userWallet", value = "userWallet 的json信息"),
    })
    public R add(@RequestBody @Validated UserWallet userWallet) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        boolean save = userWalletService.add(userId, userWallet);
        if (save){
            return R.ok();
        }
        return R.fail("新增提现地址失败");
    }

    @PostMapping("/deleteAddress")
    @ApiOperation("删除用户指定币种的提现地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addressId", value = "提现地址的ID"),
            @ApiImplicitParam(name = "payPassword", value = "交易密码"),
    })
    public R delete(@RequestParam(required = true) Long addressId, @RequestParam(required = true) String payPassword) {
        boolean isOk = userWalletService.delete(addressId, payPassword);
        if (isOk){
            return R.ok("删除成功");
        }
        return R.ok("删除失败");
    }
}
