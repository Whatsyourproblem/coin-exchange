package com.domain;

/**
    * 用户权限配置
    */
public class SysRolePrivilegeUser {
    private Long id;

    /**
    * 角色Id
    */
    private Long roleId;

    /**
    * 用户Id
    */
    private Long userId;

    /**
    * 权限Id
    */
    private Long privilegeId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPrivilegeId() {
        return privilegeId;
    }

    public void setPrivilegeId(Long privilegeId) {
        this.privilegeId = privilegeId;
    }
}