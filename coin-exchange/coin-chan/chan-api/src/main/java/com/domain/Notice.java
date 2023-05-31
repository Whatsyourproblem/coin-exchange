package com.domain;

import java.util.Date;

/**
    * 系统资讯公告信息
    */
public class Notice {
    private Long id;

    /**
    * 标题
    */
    private String title;

    /**
    * 简介
    */
    private String description;

    /**
    * 作者
    */
    private String author;

    /**
    * 文章状态
    */
    private Integer status;

    /**
    * 文章排序，越大越靠前
    */
    private Integer sort;

    /**
    * 内容
    */
    private String content;

    /**
    * 最后修改时间
    */
    private Date lastUpdateTime;

    /**
    * 创建日期
    */
    private Date created;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}