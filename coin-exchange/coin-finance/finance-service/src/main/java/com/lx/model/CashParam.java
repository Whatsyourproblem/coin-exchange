package com.lx.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel(value = "Cash购买时的表单参数")
public class CashParam {

    @ApiModelProperty(value = "币种id")
    @NotNull
    private Long coinId;


    @ApiModelProperty(value = "币种数量")
    @NotNull
    private BigDecimal num;

    @ApiModelProperty(value = "币种金额")
    @NotNull
    private BigDecimal mum;
}
