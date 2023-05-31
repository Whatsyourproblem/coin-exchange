package com.lx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lx.domain.Config;

public interface ConfigService extends IService<Config>{

    /**
     * 条件分页查询后台参数
     * @param page 分页参数
     * @param type 后台参数类型
     * @param name 后台参数名称
     * @param code 后台参数code
     **/
    Page<Config> findByPage(Page<Config> page, String type, String name, String code);

    /*
    *  通过规则的Code 来查询签名
    * */
    Config getConfigByCode(String code);
}
