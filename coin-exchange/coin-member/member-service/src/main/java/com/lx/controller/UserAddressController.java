package com.lx.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.CoinWithdraw;
import com.lx.domain.UserAddress;
import com.lx.model.R;
import com.lx.service.UserAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@Api(tags = "用户钱包地址")
@RequestMapping("/userAddress")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    @GetMapping
    @ApiOperation(value = "查询用户的钱包地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户的Id"),
            @ApiImplicitParam(name = "current",value = "当前页"),
            @ApiImplicitParam(name = "size",value = "每一页显示的条数"),
    })
    public R<Page<UserAddress>> findByPage(@ApiIgnore Page<UserAddress> page,Long userId){
        page.addOrder(OrderItem.desc("last_update_time"));
        Page<UserAddress> userAddressPage = userAddressService.findByPage(page,userId);
        return R.ok(userAddressPage);
    }

    @GetMapping("/getCoinAddress/{coinId}")
    @ApiOperation(value = "查询用户某种币的钱包地址")
    public R<String> getCoinAddress(@PathVariable("coinId") Long coinId){
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        UserAddress userAddress = userAddressService.getUserAddressByUserIdAndCoinId(coinId,userId);
        return R.ok(userAddress.getAddress());
    }

    @PostMapping("/withdraw")
    @ApiOperation(value = "用户对某种币提现到钱包")
    public R withdrawCoin(@RequestBody @Validated CoinWithdraw coinWithdraw){
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        boolean isOk = userAddressService.withdrawCoin(userId,coinWithdraw);
        if (isOk){
            return R.ok("提现成功");
        }
        return R.fail("提现失败");
    }
}
