package com.lx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.dto.CoinDto;
import com.lx.mappers.CoinMappersDto;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.domain.Coin;
import com.lx.mapper.CoinMapper;
import com.lx.service.CoinService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class CoinServiceImpl extends ServiceImpl<CoinMapper, Coin> implements CoinService{

    /*
     *  数字货币的条件分页查询
     * */
    @Override
    public Page<Coin> findByPage(String name, String type, Byte status, String title, String walletType, Page<Coin> page) {
        return page(page,
                new LambdaQueryWrapper<Coin>()
                        .like(!StringUtils.isEmpty(name),Coin::getName,name) // 名称的模糊查询
                        .like(!StringUtils.isEmpty(title),Coin::getTitle,title) // 标题的模糊查询
                        .eq(status!= null,Coin::getStatus,status)   // 状态的查询
                        .eq(!StringUtils.isEmpty(type),Coin::getType,title) // 货币类型名称的查询
                        .eq(!StringUtils.isEmpty(walletType),Coin::getWallet,walletType)    //货币钱包类型的查询
        );
    }

    /*
     *  使用币种的状态查询所有的币种信息
     * */
    @Override
    public List<Coin> getCoinsByStatus(Byte status) {
        return list(new LambdaQueryWrapper<Coin>().eq(Coin::getStatus,status));
    }

    /*
     *  使用货币的名称来查询货币
     *  货币的名称是唯一的
     * */
    @Override
    public Coin getCoinByCoinName(String coinName) {
        return getOne(new LambdaQueryWrapper<Coin>().eq(Coin::getName,coinName));
    }

    /*
     *  使用coinId的id集合,查询我们的币种
     * */
    @Override
    public List<CoinDto> findList(List<Long> coinIds) {
        List<Coin> coins = super.listByIds(coinIds);
        if (CollectionUtils.isEmpty(coins)){
            return Collections.emptyList();
        }
        List<CoinDto> coinDtos = CoinMappersDto.INSTANCE.toConvertDto(coins);
        return coinDtos;
    }
}
