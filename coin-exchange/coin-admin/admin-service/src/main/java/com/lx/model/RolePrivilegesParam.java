package com.lx.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 接收角色和权限数据
 */
@Data
@ApiModel(value = "接收角色和权限数据")
public class RolePrivilegesParam {

    @ApiModelProperty(value = "角色id")
    private Long roleId;

    @ApiModelProperty(value = "角色的权限数据id集合")
    private List<Long> privilegeIds = Collections.emptyList();
}
