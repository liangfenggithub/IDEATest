package com.easylinker.proxy.server.app.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @suthor liangfeng
 * @create 2018-09-29 16:57)
 */
@Component
public class JsonTool {
    public static JSONObject deepMerge(JSONObject source, JSONObject target) {
        Set<String> set = source.keySet();
        for (String key: set) {
            Object value = source.get(key);
            if (!target.containsKey(key)) {
                target.put(key, value);
            } else {
                if (value instanceof JSONObject) {
                    JSONObject valueJson = (JSONObject)value;
                    deepMerge(valueJson, target.getJSONObject(key));
                } else {
                    target.put(key, value);
                }
            }
        }
        return target;
    }
}
