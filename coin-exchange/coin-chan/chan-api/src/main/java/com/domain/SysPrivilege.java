package com.domain;

import java.util.Date;

/**
    * 权限配置
    */
public class SysPrivilege {
    /**
    * 主键
    */
    private Long id;

    /**
    * 所属菜单Id
    */
    private Long menuId;

    /**
    * 功能点名称
    */
    private String name;

    /**
    * 功能描述
    */
    private String description;

    private String url;

    private String method;

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

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
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