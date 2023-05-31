package com.lx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.CashRecharge;
import com.lx.domain.CoinRecharge;
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
import com.lx.mapper.CoinWithdrawMapper;
import com.lx.domain.CoinWithdraw;
import com.lx.service.CoinWithdrawService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class CoinWithdrawServiceImpl extends ServiceImpl<CoinWithdrawMapper, CoinWithdraw> implements CoinWithdrawService{

    @Autowired
    private UserServiceFeign userServiceFeign;

    /*
     *  提币记录的分页查询
     * */

    @Override
    public Page<CoinWithdraw> findByPage(Page<CoinWithdraw> page, Long coinId, Long userId, String userName, String mobile, Byte status, String numMin, String numMax, String startTime, String endTime) {
        LambdaQueryWrapper<CoinWithdraw> coinWithdrawLambdaQueryWrapper = new LambdaQueryWrapper<>();
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
            coinWithdrawLambdaQueryWrapper.in(!CollectionUtils.isEmpty(userIds), CoinWithdraw::getUserId,userIds);
        }
        // 2.若用户本次的查询中，没有带用户的信息
        coinWithdrawLambdaQueryWrapper.eq(coinId!=null,CoinWithdraw::getCoinId,coinId)
                .eq(status!=null,CoinWithdraw::getStatus,status)
                .between(
                        !(StringUtils.isEmpty(numMin)||StringUtils.isEmpty(numMax)),
                        CoinWithdraw::getNum,
                        new BigDecimal(StringUtils.isEmpty(numMin) ? "0":numMin),
                        new BigDecimal(StringUtils.isEmpty(numMax) ? "0":numMax)
                )
                .between(
                        !(StringUtils.isEmpty(startTime)||StringUtils.isEmpty(endTime)),
                        CoinWithdraw::getCreated,
                        startTime,
                        endTime + " 23:59:59"
                );
        Page<CoinWithdraw> coinWithdrawPage = page(page, coinWithdrawLambdaQueryWrapper);
        List<CoinWithdraw> records = coinWithdrawPage.getRecords();
        if (!CollectionUtils.isEmpty(records)){
            List<Long> userIds = records.stream().map(CoinWithdraw::getUserId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(basicUsers)){
                basicUsers = userServiceFeign.getBasicUsers(userIds,null,null);
            }
            Map<Long, UserDto> finalBasicUsers = basicUsers;
            records.forEach(coinWithdraw -> {
                // 需要远程调用查询用户的信息
                UserDto userDto = finalBasicUsers.get(coinWithdraw.getUserId());
                if (userDto != null){
                    coinWithdraw.setUsername(userDto.getUsername());
                    coinWithdraw.setRealName(userDto.getRealName());
                }
            });
        }
        return coinWithdrawPage;
    }

    /*
     * 查询用户某种币的提币记录
     * */
    @Override
    public Page<CoinWithdraw> findUserCoinRecharge(Page<CoinWithdraw> page, Long coinId, Long userId) {
        return page(page,new LambdaQueryWrapper<CoinWithdraw>()
                .eq(coinId!=null,CoinWithdraw::getCoinId,coinId)
                .eq(userId!=null,CoinWithdraw::getUserId,userId));
    }
}
