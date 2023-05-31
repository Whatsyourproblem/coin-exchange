package com.domain;

import java.util.Date;

/**
    * 用户角色配置
    */
public class SysUserRole {
    /**
    * 主键
    */
    private Long id;

    /**
    * 角色ID
    */
    private Long roleId;

    /**
    * 用户ID
    */
    private Long userId;

    /**
    * 创建人
    */
    private Long createBy;

    /**
    * 修改人
    */
    private Long modifyBy;

    /**
    * 创建时间
    */
    private Date created;

    /**
    * 修改时间
    */
    private Date lastUpdateTime;

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

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Long getModifyBy() {
        return modifyBy;
    }

    public void setModifyBy(Long modifyBy) {
        this.modifyBy = modifyBy;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}