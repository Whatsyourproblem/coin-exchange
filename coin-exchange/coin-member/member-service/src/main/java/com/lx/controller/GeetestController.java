package com.lx.controller;

import com.lx.geetest.GeetestLib;
import com.lx.geetest.GeetestLibResult;
import com.lx.model.R;
import com.lx.util.IpUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springside.modules.utils.net.IPUtil;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/gt")
public class GeetestController {

    @Autowired
    private GeetestLib geetestLib;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @GetMapping("/register")
    @ApiOperation(value = "获取极验的第一次数据包---")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid",value = "用户验证的一个凭证")
    })
    public R<String> register(String uuid){
        //GeetestLib gtLib = new GeetestLib(GeetestConfig.GEETEST_ID, GeetestConfig.GEETEST_KEY);
        String digestmod = "md5";
        Map<String,String> paramMap = new HashMap<String, String>();
        paramMap.put("digestmod", digestmod);
        paramMap.put("user_id", uuid);
        paramMap.put("client_type", "web");
        // 从上下文对象中获取request对象
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        paramMap.put("ip_address", IpUtil.getIpAddr(servletRequestAttributes.getRequest()));


        // 和极验服务器交互，得到响应结果
        GeetestLibResult result = geetestLib.register(digestmod, paramMap);
        // 将结果状态写到session中，此处register接口存入session，后续validate接口会取出使用
        // 注意，此demo应用的session是单机模式，格外注意分布式环境下session的应用
        // request.getSession().setAttribute(GeetestLib.GEETEST_SERVER_STATUS_SESSION_KEY, result.getStatus());
        // request.getSession().setAttribute("userId", userId);

        // 由于我们是分布式环境，所以考虑将结果状态放入到redis中
        redisTemplate.opsForValue().set(GeetestLib.GEETEST_SERVER_STATUS_SESSION_KEY,result.getStatus(),180, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(GeetestLib.GEETEST_SERVER_USER_KEY + ":" + uuid,uuid,180, TimeUnit.SECONDS);


        // 注意，不要更改返回的结构和值类型
        return R.ok(result.getData());
    }
}
