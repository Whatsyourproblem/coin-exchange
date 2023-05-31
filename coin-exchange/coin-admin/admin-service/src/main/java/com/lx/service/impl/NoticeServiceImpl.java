package com.lx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.Notice;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.mapper.NoticeMapper;
import com.lx.service.NoticeService;
import org.springframework.util.StringUtils;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService{
    /**
     * <h2>条件分页查询公告</h2>
     * @param page  分页数据
     * @param title 公告的标题
     * @param startTime 公告的创建开始时间
     * @param endTime   公告的创建结束时间时间
     * @param status    公告的状态
     **/
    @Override
    public Page<Notice> findByPage(Page<Notice> page, String title, String startTime, String endTime, Integer status) {
        return page(page, new LambdaQueryWrapper<Notice>()
                .like(!StringUtils.isEmpty(title), Notice::getTitle, title)
                .between(
                        !StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime),
                        Notice::getCreated,
                        startTime, endTime + " 23:59:59")
                .eq(status != null, Notice::getStatus, status)
        );
    }


    /*
     *  查询公告
     * */
    @Override
    public Page<Notice> findNoticeSimple(Page<Notice> page) {
        return page(page,new LambdaQueryWrapper<Notice>()
                .eq(Notice::getStatus,1)
                .orderByAsc(Notice::getSort));
    }
}
