package com.lx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.CashRecharge;
import com.lx.dto.UserDto;
import com.lx.feign.UserServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.domain.CoinRecharge;
import com.lx.mapper.CoinRechargeMapper;
import com.lx.service.CoinRechargeService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class CoinRechargeServiceImpl extends ServiceImpl<CoinRechargeMapper, CoinRecharge> implements CoinRechargeService{

    @Autowired
    private UserServiceFeign userServiceFeign;

    /*
     *  分页条件查询充币记录
     * */
    @Override
    public Page<CoinRecharge> findByPage(Page<CoinRecharge> page, Long coinId, Long userId, String userName, String mobile, Byte status, String numMin, String numMax, String startTime, String endTime) {
        LambdaQueryWrapper<CoinRecharge> coinRechargeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 1.若用户本次的查询中,带了用户的信息userId,userName,mobile ---> 本质就是要把用户的id放在我们的查询条件里面
        Map<Long, UserDto> basicUsers = null;
        if (userId!=null || !StringUtils.isEmpty(userName) || !StringUtils.isEmpty(mobile)){
            // 使用用户的信息查询
            // 需要远程调用查询用户的信息
            // 这远程调用接口不仅仅通过ids进行批量查询，还通过userName和mobile进行查询UserDto的值
            basicUsers = userServiceFeign.getBasicUsers(userId == null ? null : Arrays.asList(userId), userName, mobile);
            if (CollectionUtils.isEmpty(basicUsers)){
                // 找不到这样的用户
                return page;
            }
            Set<Long> userIds = basicUsers.keySet();
            coinRechargeLambdaQueryWrapper.in(!CollectionUtils.isEmpty(userIds), CoinRecharge::getUserId,userIds);
        }
        // 2.若用户本次的查询中，没有带用户的信息
        coinRechargeLambdaQueryWrapper.eq(coinId!=null,CoinRecharge::getCoinId,coinId)
                .eq(status!=null,CoinRecharge::getStatus,status)
                .between(
                        !(StringUtils.isEmpty(numMin)||StringUtils.isEmpty(numMax)),
                        CoinRecharge::getAmount,
                        new BigDecimal(StringUtils.isEmpty(numMin) ? "0":numMin),
                        new BigDecimal(StringUtils.isEmpty(numMax) ? "0":numMax)
                )
                .between(
                        !(StringUtils.isEmpty(startTime)||StringUtils.isEmpty(endTime)),
                        CoinRecharge::getCreated,
                        startTime,
                        endTime + " 23:59:59"
                );
        Page<CoinRecharge> coinRechargePage = page(page, coinRechargeLambdaQueryWrapper);
        List<CoinRecharge> records = coinRechargePage.getRecords();
        if (!CollectionUtils.isEmpty(records)){
            List<Long> userIds = records.stream().map(CoinRecharge::getUserId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(basicUsers)){
                basicUsers = userServiceFeign.getBasicUsers(userIds,null,null);
            }
            Map<Long, UserDto> finalBasicUsers = basicUsers;
            records.forEach(coinRecharge -> {
                // 需要远程调用查询用户的信息
                UserDto userDto = finalBasicUsers.get(coinRecharge.getUserId());
                if (userDto != null){
                    coinRecharge.setUsername(userDto.getUsername());
                    coinRecharge.setRealName(userDto.getRealName());
                }
            });
        }
        return coinRechargePage;
    }

    /*
     *  查询用户的充币记录
     * */
    @Override
    public Page<CoinRecharge> findUserCoinRecharge(Page<CoinRecharge> page, Long coinId, Long userId) {
        return page(page,new LambdaQueryWrapper<CoinRecharge>()
                .eq(coinId!=null,CoinRecharge::getCoinId,coinId)
                .eq(userId!=null,CoinRecharge::getUserId,userId));
    }
}
