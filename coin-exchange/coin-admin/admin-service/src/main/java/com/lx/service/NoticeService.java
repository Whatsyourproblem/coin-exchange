package com.lx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lx.domain.Notice;

public interface NoticeService extends IService<Notice>{
    /**
     * 条件分页查询公告
     * @param page  分页数据
     * @param title 公告的标题
     * @param startTime 公告的创建开始时间
     * @param endTime   公告的创建结束时间时间
     * @param status    公告的状态
     **/
    Page<Notice> findByPage(Page<Notice> page, String title, String startTime, String endTime, Integer status);

    /*
    *  查询公告
    * */
    Page<Notice> findNoticeSimple(Page<Notice> page);
}
