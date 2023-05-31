package com.lx.controller;

import com.lx.domain.Sms;
import com.lx.model.R;
import com.lx.service.SmsService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    @PostMapping("/sendTo")
    @ApiOperation(value = "发送短信")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sms",value = "smsjson数据")
    })
    public R sendSms(@RequestBody Sms sms){
        boolean isOk = smsService.sendSms(sms);
        if (isOk){
            return R.ok();
        }
        return R.fail("发送失败");
    }
}
