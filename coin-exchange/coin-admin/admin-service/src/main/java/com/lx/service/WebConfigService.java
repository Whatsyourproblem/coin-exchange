package com.lx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lx.domain.WebConfig;

import java.util.List;

public interface WebConfigService extends IService<WebConfig>{

    /**
     * 分页查询我们的资源配置(webConfig)
     * @param page webConfig的名称
     * @param name webConfig的类型
     * @param type
     **/
    Page<WebConfig> findByPage(Page<WebConfig> page, String name, String type);

    /*
    *  查询pc端的banner图
    * */
    List<WebConfig> getPcBanner();
}
