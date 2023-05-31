package com.domain;

import java.util.Date;

/**
    * 系统菜单
    */
public class SysMenu {
    /**
    * 主键
    */
    private Long id;

    /**
    * 上级菜单ID
    */
    private Long parentId;

    /**
    * 上级菜单唯一KEY值
    */
    private String parentKey;

    /**
    * 类型 1-分类 2-节点
    */
    private Byte type;

    /**
    * 名称
    */
    private String name;

    /**
    * 描述
    */
    private String desc;

    /**
    * 目标地址
    */
    private String targetUrl;

    /**
    * 排序索引
    */
    private Integer sort;

    /**
    * 状态 0-无效； 1-有效；
    */
    private Byte status;

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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
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