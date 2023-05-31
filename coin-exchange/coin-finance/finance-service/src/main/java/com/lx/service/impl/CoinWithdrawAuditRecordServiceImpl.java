package com.lx.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.domain.CoinWithdrawAuditRecord;
import com.lx.mapper.CoinWithdrawAuditRecordMapper;
import com.lx.service.CoinWithdrawAuditRecordService;
@Service
public class CoinWithdrawAuditRecordServiceImpl extends ServiceImpl<CoinWithdrawAuditRecordMapper, CoinWithdrawAuditRecord> implements CoinWithdrawAuditRecordService{

}
