package com.lx.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 用户财产记录
    */
@ApiModel(value="com-lx-domain-Account")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "account")
public class Account {
    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="自增id")
    private Long id;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value="用户id")
    private Long userId;

    /**
     * 币种id
     */
    @TableField(value = "coin_id")
    @ApiModelProperty(value="币种id")
    private Long coinId;

    /**
     * 账号状态：1，正常；2，冻结；
     */
    @TableField(value = "status")
    @ApiModelProperty(value="账号状态：1，正常；2，冻结；")
    private Boolean status;

    @TableField(value = "balance_amount")
    @ApiModelProperty(value="")
    private BigDecimal balanceAmount;

    @TableField(value = "freeze_amount")
    @ApiModelProperty(value="")
    private BigDecimal freezeAmount;

    @TableField(value = "recharge_amount")
    @ApiModelProperty(value="")
    private BigDecimal rechargeAmount;

    @TableField(value = "withdrawals_amount")
    @ApiModelProperty(value="")
    private BigDecimal withdrawalsAmount;

    @TableField(value = "net_value")
    @ApiModelProperty(value="")
    private BigDecimal netValue;

    @TableField(value = "lock_margin")
    @ApiModelProperty(value="")
    private BigDecimal lockMargin;

    @TableField(value = "float_profit")
    @ApiModelProperty(value="")
    private BigDecimal floatProfit;

    @TableField(value = "total_profit")
    @ApiModelProperty(value="")
    private BigDecimal totalProfit;

    @TableField(value = "rec_addr")
    @ApiModelProperty(value="")
    private String recAddr;

    /**
     * 版本号
     */
    @TableField(value = "version")
    @ApiModelProperty(value="版本号")
    private Long version;

    /**
     * 更新时间
     */
    @TableField(value = "last_update_time")
    @ApiModelProperty(value="更新时间")
    private Date lastUpdateTime;

    /**
     * 创建时间
     */
    @TableField(value = "created")
    @ApiModelProperty(value="创建时间")
    private Date created;

    /**
     * 卖出比率
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "卖出比率")
    private BigDecimal sellRate;

    /**
     * 买入比率
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "买入比率")
    private BigDecimal buyRate;

    /**
     * 币种信息
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "币种信息")
    private Coin coin;

}