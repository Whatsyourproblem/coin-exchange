package com.domain;

import java.util.Date;

/**
    * 系统日志
    */
public class SysUserLog {
    /**
    * 主键
    */
    private Long id;

    /**
    * 组
    */
    private String group;

    /**
    * 用户Id
    */
    private Long userId;

    /**
    * 日志类型 1查询 2修改 3新增 4删除 5导出 6审核
    */
    private Short type;

    /**
    * 方法
    */
    private String method;

    /**
    * 参数
    */
    private String params;

    /**
    * 时间
    */
    private Long time;

    /**
    * IP地址
    */
    private String ip;

    /**
    * 描述
    */
    private String description;

    /**
    * 备注
    */
    private String remark;

    /**
    * 创建时间
    */
    private Date created;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}