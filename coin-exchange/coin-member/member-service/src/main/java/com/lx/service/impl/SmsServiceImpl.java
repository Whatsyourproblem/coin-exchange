package com.lx.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.alicloud.sms.ISmsService;
import com.alibaba.fastjson.JSON;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.lx.domain.Config;
import com.lx.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.domain.Sms;
import com.lx.mapper.SmsMapper;
import com.lx.service.SmsService;
@Service
@Slf4j
public class SmsServiceImpl extends ServiceImpl<SmsMapper, Sms> implements SmsService{

    @Autowired
    private ISmsService smsService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /*
    * 发送短信
    * */
    @Override
    public boolean sendSms(Sms sms) {
        log.info("发送短信{}", JSON.toJSONString(sms,true));
        SendSmsRequest request = buildSmsRequest(sms);
        try {
            SendSmsResponse sendSmsResponse = smsService.sendSmsRequest(request);
            log.info("发送的结果为{}",JSON.toJSONString(sendSmsResponse,true));
            String code = sendSmsResponse.getCode();
            if ("OK".equals(code)){
                // 发送成功,否则失败
                sms.setStatus(1);
                return save(sms);
            }else {
                return false;
            }

        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
    *  构建发送短信的请求对象
    * */
    private SendSmsRequest buildSmsRequest(Sms sms) {
        SendSmsRequest sendSmsRequest = new SendSmsRequest();
        sendSmsRequest.setPhoneNumbers(sms.getMobile()); //发送给谁

        // 这里的configService使用的是公共表,即所有微服务都使用的表
        // 查询数据表来获取签名
        Config signConfig = configService.getConfigByCode("SIGN");
        sendSmsRequest.setSignName(signConfig.getValue()); // 设置签名---公司里面不会随便的改变--Config里查询签名
        // 查询数据库表来获取模板
        Config configByCode = configService.getConfigByCode(sms.getTemplateCode());
        if (configByCode == null){
            throw new IllegalArgumentException("您输入的签名不存在");
        }
        sendSmsRequest.setTemplateCode(configByCode.getValue()); // 模板的Code 动态改变

        // 随机生成验证码
        String code = RandomUtil.randomNumbers(6);
        // 需要把code 保存到redis 里面
        // key: SMS:VERIFY_OLD_PHONE:15236966937    value: 123456
        redisTemplate.opsForValue().set("SMS:" + sms.getTemplateCode() + ":" + sms.getMobile(),code, 5,TimeUnit.MINUTES);
        sendSmsRequest.setTemplateParam("{\"code\":\"" + code + "\"}");

        // 将查询到的短信模板中的 ${code} 替换成我们自己生成的code
        String desc = configByCode.getDesc(); // [sign]您的验证码${code}，该验证码5分钟内有效，请勿泄漏于他人!
        String content = signConfig.getValue() + ":" + desc.replaceAll("\\$\\{code\\}",code);
        sms.setContent(content); // 最后短信的内容
        return sendSmsRequest;
    }
}
