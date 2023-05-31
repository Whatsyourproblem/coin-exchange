package com.lx.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.joda.time.DateTime;
import org.tio.websocket.common.WsResponse;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseEntity {

    /*
    *  订阅的组名
    * */
    private String subbed;

    /*
    *  取消订阅的组名
    * */
    private String canceled;

    /*
    *  发送的事件
    * */
    private String event;

    /*
    *  推送的id
    * */
    private String id;

    /*
    * 推送的channel
    * */
    private String ch;

    /*
    *  状态
    * */
    private String status;

    public Long getTs() {
        return new DateTime().getMillis();
    }

    private Long  ts;

    private Map<String, Object> extend = new LinkedHashMap<>();

    public WsResponse build() {
        extend.put("id", this.getId());
        extend.put("ch", this.getCh());
        extend.put("status", this.getStatus());
        extend.put("subbed", this.getSubbed());
        extend.put("canceled", this.getCanceled());
        extend.put("event", this.getEvent());
        extend.put("ts", this.getTs());
        return WsResponse.fromText(JSONObject.toJSONString(extend), "utf-8");
    }

    public ResponseEntity put(String key, Object value) {
        extend.put(key, value);
        return this;
    }

    public ResponseEntity putAll(Map<String, Object> m) {
        extend.putAll(m);
        return this;
    }
}