package com.lx.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel(value = "GCN卖出的参数")
public class CashSellParam {

    @ApiModelProperty(value = "币种id")
    @NotNull
    private Long coinId;


    @ApiModelProperty(value = "币种数量")
    @NotNull
    private BigDecimal num;

    @ApiModelProperty(value = "币种金额")
    @NotNull
    private BigDecimal mum;

    /**
     * 支付密码
     */
    @ApiModelProperty(value = "支付密码")
    @NotBlank
    private String payPassword;

    /**
     * 手机验证码
     */
    @ApiModelProperty(value = "手机验证码")
    @NotBlank
    private String validateCode;
}
