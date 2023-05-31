package com.lx.service;

import com.lx.domain.UserAuthAuditRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserAuthAuditRecordService extends IService<UserAuthAuditRecord>{


    /*
    *  获取一个用户的审核记录
    * */
    List<UserAuthAuditRecord> getUserAuthAuditRecordList(Long id);
}
