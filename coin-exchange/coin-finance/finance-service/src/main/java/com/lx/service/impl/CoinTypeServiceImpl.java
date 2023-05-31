package com.lx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.mapper.CoinTypeMapper;
import com.lx.domain.CoinType;
import com.lx.service.CoinTypeService;
import org.springframework.util.StringUtils;

@Service
public class CoinTypeServiceImpl extends ServiceImpl<CoinTypeMapper, CoinType> implements CoinTypeService{

    /*
     *  条件分页查询货币类型
     * */
    @Override
    public Page<CoinType> findByPage(Page<CoinType> page, String code) {
        return page(page,new LambdaQueryWrapper<CoinType>()
                .like(!StringUtils.isEmpty(code), CoinType::getCode,code));
    }

    /*
     *  使用币种的状态查询所有的币种类型值
     * */
    @Override
    public List<CoinType> listByStatus(Byte status) {
        return list(new LambdaQueryWrapper<CoinType>().eq(status!=null,CoinType::getStatus,status));
    }
}
