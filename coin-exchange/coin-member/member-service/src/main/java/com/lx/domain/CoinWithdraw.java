package com.lx.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("用户提现货币字段")
public class CoinWithdraw {

    @ApiModelProperty("地址id")
    @NotNull
    private Long addressId;

    @ApiModelProperty("提现货币数量")
    @NotBlank
    private String amount;

    @ApiModelProperty("币种id")
    @NotNull
    private Long coinId;

    @ApiModelProperty("支付密码")
    @NotBlank
    private String payPassword;

    @ApiModelProperty("验证码")
    @NotBlank
    private String verifyCode;
}
