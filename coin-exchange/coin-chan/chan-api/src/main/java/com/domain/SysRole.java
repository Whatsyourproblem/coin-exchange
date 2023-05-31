package com.domain;

import java.util.Date;

/**
    * 角色
    */
public class SysRole {
    /**
    * 主键
    */
    private Long id;

    /**
    * 名称
    */
    private String name;

    /**
    * 代码
    */
    private String code;

    /**
    * 描述
    */
    private String description;

    /**
    * 创建人
    */
    private Long createBy;

    /**
    * 修改人
    */
    private Long modifyBy;

    /**
    * 状态0:禁用 1:启用
    */
    private Byte status;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
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