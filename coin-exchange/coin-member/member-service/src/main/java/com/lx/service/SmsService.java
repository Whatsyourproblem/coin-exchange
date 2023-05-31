package com.lx.service;

import com.lx.domain.Sms;
import com.baomidou.mybatisplus.extension.service.IService;
public interface SmsService extends IService<Sms>{


    /*
    *  发送一个短信
    * */
    boolean sendSms(Sms sms);
}
