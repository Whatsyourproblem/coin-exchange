package com.lx.aspect;


import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.lx.model.WebLog;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.aspectj.lang.reflect.MethodSignature;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * web日志记录
 */
@Component
@Aspect
@Order(1)
@Slf4j
public class WebLogAspect {

    /**
     * <h2>
     *  1.定义切入点:
     *  controller 包里面所有类，类里面的所有方法，都有该切面
     * </h2>
     **/
    @Pointcut("execution(* com.lx.controller.*.*(..))")
    public void webLog() {
    }

    /**
     * <h2>
     *     2.记录日志的环绕通知
     * </h2>
     * 也就是方法执行之前、之后都可以记录日志
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
        // 获取当前请求的request对象
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        //获取安全上下文
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //*************************************weblog信息***********************************************
        //设置URI
        webLog.setUri(request.getRequestURI());
        //设置URL
        String url = request.getRequestURL().toString();
        //http://ip:port ip地址以及端口号
        webLog.setUrl(url);
        // 截取后缀（获得后缀）
        webLog.setBasePath(StrUtil.removeSuffix(url, URLUtil.url(url).getPath()));
        //获取用户的id 需要先获取当前登录状态的上下文
        webLog.setUsername(authentication == null ? "anonymous" : authentication.getPrincipal().toString());
        //设置ip地址
        webLog.setIp(request.getRemoteAddr()); // TODO 获取ip地址

        //因为我们使用swagger工具，所有必须在方法上添加@ApiOperation(value="")这个注解,我们可以获取这个注解中的value值，放入到setDescription()中
        //方法信息描述
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        //类的全路径
        String className = proceedingJoinPoint.getTarget().getClass().getName();
        //方法
        Method method = signature.getMethod();
        // 获取方法上面的@ApiOperation注解
        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
        webLog.setDescription(apiOperation == null ? "no desc " : apiOperation.value());
        webLog.setMethod(className + "." + method.getName());
        // 将方法的参数转化为键值对的形式 {"key_参数的名称":"value_参数的值"}
        webLog.setParameter(getMethodParameter(method, proceedingJoinPoint.getArgs()));
        webLog.setResult(result);
        //************************************************************************************
        log.info("访问日志: " + JSON.toJSONString(webLog, true));
        return result;
    }

    /**
     * <h2>获取方法的参数</h2>
     * @param method
     * @param args {"key_参数的名称":"value_参数的值"}
     **/
    private Object getMethodParameter(Method method, Object[] args) {
        Map<String, Object> methodParametersWithValues = new HashMap<>();
        LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
        //方法的形参名称
        String[] parameterNames = discoverer.getParameterNames(method);
        for (int i = 0; i < parameterNames.length; i++) {
            // 如果参数为 password 或者 file 则跳过本次循环
            if (parameterNames[i].equals("password") || parameterNames[i].equals("file")) {
                //continue;
                // 放入方法的参数
                methodParametersWithValues.put(parameterNames[i], "受限的支持类型");
            }else {
                // 放入方法的参数
                methodParametersWithValues.put(parameterNames[i], args[i]);
            }

        }
        return methodParametersWithValues;
    }

}
