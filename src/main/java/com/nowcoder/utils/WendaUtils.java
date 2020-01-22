package com.nowcoder.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class WendaUtils {
    // 生成随机字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    private  static  final Logger logger = LoggerFactory.getLogger(WendaUtils.class);
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
    public static String getJSONString(int code, String msg){
        JSONObject json  = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        return json.toJSONString();
    }
    public static String getJSONString(int code){
        JSONObject json  = new JSONObject();
        json.put("code", code);
        return json.toJSONString();
    }
}
