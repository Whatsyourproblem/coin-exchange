package com.lx.model;

import com.lx.domain.DepthItemVo;
import com.lx.enums.OrderDirection;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * 交易的盘口数据,以后前端可以查询该数据
 */
@Data
public class TradePlate {

    /**
     * 判断数据的详情
     */
    private LinkedList<DepthItemVo> items = new LinkedList<DepthItemVo>();
    /**
     * 最大支持的深度
     */
    private int maxDepth = 100;

    /**
     * 订单的方向
     */
    private OrderDirection direction;

    /**
     * 交易对
     */
    private String symbol;

    public TradePlate(String symbol, OrderDirection direction) {
        this.symbol = symbol;
        this.direction = direction;
    }


    /**
     * 添加订单数据到盘口数据里面
     * 当我们新增一个委托单时: 它没有全部的成交 ,因此把它展示在盘口里面
     *
     * @param order
     */
    public void add(Order order) {
        if (order.getOrderDirection() != direction) {
            return;
        }
        int i = 0;
        for (i = 0; i < items.size(); i++) {
            // 1 我们的sell 队列是: 从小到大
            // 2 我们的buy 队列是: 从大到小
            DepthItemVo depthItemVo = items.get(i);
            if (
                    // 盘口价格小于我出的委托单价格(我出的价格可能高得多)
                    (direction == OrderDirection.BUY && order.getPrice().compareTo(depthItemVo.getPrice()) == -1)
                            ||
                            // 盘口价格大于我出的价格(我卖的价格可能低得多)
                            (direction == OrderDirection.SELL && order.getPrice().compareTo(depthItemVo.getPrice()) == 1)

            ) {
                // 贪心(重点)
                // 在买入的时候 以最小的价格买入
                // 在卖出的时候,以最大的价格卖出
                // 还不能插入,往前走一步(进一步扩大利益)
                continue;
            } else if (depthItemVo.getPrice().compareTo(order.getPrice()) == 0) {
                // 我出的价格刚好与盘口价格相等,直接买入,无需等待
                depthItemVo.setVolume(depthItemVo.getVolume().add(order.getAmount().subtract(order.getTradedAmount())));
                return;
            } else {
                break; // 我就想插入(用当前出的钱 买或者卖) 当前我就在第 i个位置
            }
        }

        /*
        *  当前深度小于最大深度,可以进行买入或者卖出
        * */
        if (i < maxDepth) {
            DepthItemVo depthItemVo = new DepthItemVo();
            depthItemVo.setPrice(order.getPrice());
            depthItemVo.setVolume(order.getAmount().subtract(order.getTradedAmount()));
            items.add(i, depthItemVo);
        }
    }


    /**
     * 从盘口里面移除订单
     *
     * @param order
     */
    public void remove(Order order) {
        // order.getAmount().subtract(order.getTradedAmount() 成交的数量
        remove(order, order.getAmount().subtract(order.getTradedAmount()));
    }

    /***
     * 从盘口里面移除数据
     * @param order
     * @param amount
     */
    public void remove(Order order, BigDecimal amount) {

        if (items.size() == 0) {
            return;
        }
        if (order.getOrderDirection() != direction) {
            return;
        }
        /*
        *  判断该盘口的委托量能否被吃完,吃完则移除
        * */
        Iterator<DepthItemVo> iterator = items.iterator();
        while (iterator.hasNext()) {
            DepthItemVo next = iterator.next();
            if (order.getPrice().compareTo(next.getPrice()) == 0) { // 价格相同
                // 你的出价与盘口的价格一致，则减去盘口数据的数量
                next.setVolume(next.getVolume().subtract(amount));
                if (next.getVolume().compareTo(BigDecimal.ZERO) <= 0) {
                    iterator.remove(); // 若价格为 0 后,我们直接可以摘掉它
                }
            }

        }

    }
}
