package com.lx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.mapper.ConfigMapper;
import com.lx.service.ConfigService;
import org.springframework.util.StringUtils;
import com.lx.domain.Config;


@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService{
    /**
     * <h2>条件分页查询后台参数</h2>
     *
     * @param page 分页参数
     * @param type 后台参数类型
     * @param name 后台参数名称
     * @param code 后台参数code
     **/
    @Override
    public Page<Config> findByPage(Page<Config> page, String type, String name, String code) {
        return page(page, new LambdaQueryWrapper<Config>()
                .like(!StringUtils.isEmpty(type), Config::getType, type)
                .like(!StringUtils.isEmpty(name), Config::getName, name)
                .like(!StringUtils.isEmpty(code), Config::getCode, code)
        );
    }

    @Override
    public Config getConfigByCode(String code) {
        return getOne(new LambdaQueryWrapper<Config>().eq(Config::getCode,code));
    }
}
