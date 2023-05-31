package com.lx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lx.domain.WorkIssue;

public interface WorkIssueService extends IService<WorkIssue>{

    /**
     * 分页条件查询工单
     * @param page       分页参数
     * @param status     工单当前的处理状态
     * @param startTime  工单创建的起始时间
     * @param endTime    工单创建的截至时间
     **/
    Page<WorkIssue> findByPage(Page<WorkIssue> page, Integer status, String startTime, String endTime);

    /*
    *  前台系统查询客户工单
    * */
    Page<WorkIssue> getIssueList(Page<WorkIssue> page,Long userId);
}
