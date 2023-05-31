package com.lx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.WebConfig;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.mapper.WebConfigMapper;
import com.lx.service.WebConfigService;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class WebConfigServiceImpl extends ServiceImpl<WebConfigMapper, WebConfig> implements WebConfigService{
    /**
     * <h2>分页查询我们的资源配置(webConfig)</h2>
     * @param page webConfig的名称
     * @param name webConfig的类型
     * @param type
     **/
    @Override
    public Page<WebConfig> findByPage(Page<WebConfig> page, String name, String type) {
        return page(page, new LambdaQueryWrapper<WebConfig>()
                .like(!StringUtils.isEmpty(name), WebConfig::getName, name)
                .eq(!StringUtils.isEmpty(type), WebConfig::getType, type)
        );

    }

    /*
     *  查询pc端的banner图
     * */
    @Override
    public List<WebConfig> getPcBanner() {
        return list(new LambdaQueryWrapper<WebConfig>()
                .eq(WebConfig::getType,"WEB_BANNER")
                .eq(WebConfig::getStatus,1)
                .orderByAsc(WebConfig::getSort)
        );
    }
}
