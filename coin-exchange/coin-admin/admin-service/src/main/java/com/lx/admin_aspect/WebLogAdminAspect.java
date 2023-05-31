package com.lx.admin_aspect;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.lx.domain.SysUserLog;
import com.lx.model.WebLog;
import com.lx.service.SysUserLogService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


//@Component
@Aspect
@Order(2)
@Slf4j
public class WebLogAdminAspect {

    /**
     * 雪花算法
     *  参数一：机器id
     *  参数二：应用id
     **/
    private final Snowflake snowflake = new Snowflake(1, 1);

    @Autowired
    private SysUserLogService sysUserLogService;


    /**
     *  定义切入点:
     *  controller 包里面所有类，类里面的所有方法，都有该切面
     **/
    @Pointcut("execution(* com.lx.controller.*.*(..))")
    public void webLog() {
    }

    /**
     * 记录日志的环绕通知
     * @param
     **/
    @Around("webLog()")
    public Object recodeWebLog(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = null;
        WebLog webLog = new WebLog();
        long start = System.currentTimeMillis();
        //执行方法的真实调用
        result = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        long end = System.currentTimeMillis();
        // 请求该接口花费的时间
        webLog.setSpendTime((int) ((end - start) / 1000));
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        //获取安全上下文
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //*************************************weblog信息***********************************************
        //设置URI
        webLog.setUri(request.getRequestURI());
        //设置URL
        String url = request.getRequestURL().toString();
        webLog.setUrl(url);
        //http://ip:port
        webLog.setBasePath(StrUtil.removeSuffix(url, URLUtil.url(url).getPath()));
        //获取用户的id
        webLog.setUsername(authentication == null ? "anonymous" : authentication.getPrincipal().toString());
        //设置ip地址
        webLog.setIp(request.getRemoteAddr());

        //因为我们使用swagger工具，所有必须在方法上添加@ApiOperation(value="")这个注解
        //方法信息描述
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        //类的全路径
        String className = proceedingJoinPoint.getTarget().getClass().getName();
        //方法
        Method method = signature.getMethod();
        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
        webLog.setDescription(apiOperation == null ? "no desc " : apiOperation.value());
        webLog.setMethod(className + "." + method.getName());
        webLog.setParameter(getMethodParameter(method, proceedingJoinPoint.getArgs()));
        webLog.setResult(result);
        //************************************************************************************
        log.info("访问日志: " + JSON.toJSONString(webLog, true));
        //************************************************************************************
        //保存日志到数据库
        SysUserLog sysUserLog = new SysUserLog();
        sysUserLog.setId(snowflake.nextId());
        sysUserLog.setCreated(new Date());
        //描述
        sysUserLog.setDescription(webLog.getDescription());
        sysUserLog.setGroup(webLog.getDescription());
        sysUserLog.setUserId(Long.valueOf(webLog.getUsername()));
        sysUserLog.setMethod(webLog.getMethod());
        sysUserLog.setIp(webLog.getIp());
        sysUserLogService.save(sysUserLog);
        //************************************************************************************
        return result;
    }

    /**
     * 获取方法的参数
     * @param method
     * @param args
     **/
    private Object getMethodParameter(Method method, Object[] args) {
        Map<String, Object> methodParametersWithValues = new HashMap<>();
        LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
        //方法名称
        String[] parameterNames = discoverer.getParameterNames(method);
        for (int i = 0; i < parameterNames.length; i++) {
            methodParametersWithValues.put(parameterNames[i], args[i]);
        }
        return methodParametersWithValues;
    }
}
