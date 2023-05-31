package com.lx.enums;

import lombok.Getter;

@Getter
public enum WorkIssueStatusEnum {
    ANSWERING(1, "待回答"),
    ANSWERED(2, "已回答");
    private Integer status;
    private String desc;

    WorkIssueStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}
