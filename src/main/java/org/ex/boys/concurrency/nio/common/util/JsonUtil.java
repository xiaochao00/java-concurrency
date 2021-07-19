package org.ex.boys.concurrency.nio.common.util;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * json util
 *
 * @author shichao
 * @since 1.0.0
 * 2021/7/20 0:36
 */
public class JsonUtil {
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    private JsonUtil() {
    }

    public static String pojoToJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T jsonToPojo(String json, Class<T> tClass) {
        return JSONObject.parseObject(json, tClass);
    }
}
