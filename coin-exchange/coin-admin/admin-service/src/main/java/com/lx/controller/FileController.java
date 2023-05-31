package com.lx.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.lx.model.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.unit.DataUnit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/*
*  完成文件上传的功能
* */
@RestController
@Api(tags = "文件上传的控制器")

public class FileController {

    @Autowired
    private OSS ossClient;  // spring-cloud-alibaba-oss 会自动的注入该对象，报红没问题

    @Value("${spring.cloud.alicloud.access-key}")
    private String accessId;

    @Value("${oss.bucket.name:coin-exchange-lx}")
    private String bucketName;

    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endPoint;

    // 回调地址
    @Value("${oss.callback.url:http://47.115.222.96:9000}")
    private String ossCallBackUrl;


    @ApiOperation(value = "上传文件")
    @PostMapping("/image/AliYunImgUpload")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file",value = "你要上传的文件")
    })
    public R<String> fileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        /*
        *  1.bucketName
        *  2.文件的名称 日期+原始的文件名(uuid)来做
        *  3.文件的输入流
        * */
        String fileName = DateUtil.today().replaceAll("-","/") + "/" + file.getOriginalFilename();
        ossClient.putObject(bucketName,fileName,file.getInputStream());
        // https://coin-exchange-imgs.oss-cn-beijing.aliyuncs.com/2020/10/10/xxx.jpg
        return R.ok("https://"+ bucketName + "." + endPoint + "/" + fileName); //http://xxx.com/路径 能使用浏览器访问到的文件路径
    }

    @GetMapping("/image/pre/upload")
    @ApiOperation(value = "文件的上传获取票据")
    public R<Map<String,String>> preUploadPolicy(){
        String dir = DateUtil.today().replaceAll("-","/") + "/";
        Map<String, String> map = getPolicy(30L, 3 * 1024 * 1024L, dir);
        return R.ok(map);
    }

    private Map<String, String> getPolicy(long expireTime, long maxFileSize, String dir){
        try {
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            // PostObject请求最大可支持的文件大小为5GB,即CONTENT_LENGTH_RANGE为5*1024*1024*1024
            PolicyConditions policyConds = new PolicyConditions();
            // 设置上传文件的大小
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, maxFileSize);
            // 设置上传文件上传到哪一个文件夹
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            // 设置Policy(票据)的有效时间
            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            // 返回值
            Map<String, String> respMap = new LinkedHashMap<String, String>();
            // 设置我们之前的accessKey
            respMap.put("accessId", accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            // 设置上传的主机  http://bulketName.endpoint
            respMap.put("host", "https://"+bucketName + "." +endPoint);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            // respMap.put("expire", formatISO8601Date(expiration));

            JSONObject jasonCallback = new JSONObject();
            // 当我们的前端把文件上传到oss服务器成功后,oss服务器会向回调的callbackUrl发一个post请求来告诉
            // 告诉我们的后端服务器用户上传文件的情况,该地址是公网地址
            jasonCallback.put("callbackUrl", ossCallBackUrl);
            jasonCallback.put("callbackBody",
                    "filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}");
            jasonCallback.put("callbackBodyType", "application/x-www-form-urlencoded");
            String base64CallbackBody = BinaryUtil.toBase64String(jasonCallback.toString().getBytes());
            respMap.put("callback", base64CallbackBody);
            return respMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
