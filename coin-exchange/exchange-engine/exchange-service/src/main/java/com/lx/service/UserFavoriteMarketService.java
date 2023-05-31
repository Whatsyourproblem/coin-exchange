package com.lx.service;

import com.lx.domain.UserFavoriteMarket;
import com.baomidou.mybatisplus.extension.service.IService;
public interface UserFavoriteMarketService extends IService<UserFavoriteMarket>{


    /*
    *  用户取消收藏
    * */
    boolean deleteUserFavoriteMarket(Long marketId, Long userId);
}
