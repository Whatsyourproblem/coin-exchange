package com.lx.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum  ExceptionEnum {

    /**
     * 默认错误类型
     */
    DEFAULT(500, "系统繁忙，请稍后再试！"),
    COIN_TYPE_ADD_FAIL(501001, "新增币种类型失败"),
    COIN_TYPE_UPDATE_FAIL(501002, "修改币种类型失败"),
    COIN_TYPE_SET_STATUS_FAIL(501004, "设置币种类型状态失败"),
    COIN_SET_STATUS_FAIL(502004, "设置数字货币状态失败"),
    COIN_CONFIG_UPDATE_FAIL(503002, "修改数字货币配置失败"),
    ADMIN_ADDRESS_ADD_FAIL(504001, "新增归集地址失败"),
    ADMIN_ADDRESS_UPDATE_FAIL(504002, "修改归集地址失败"),
    ;

    /**
     * 错误码
     */
    private int code;

    /**
     * 错误信息
     */
    private String msg;
}
