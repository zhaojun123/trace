package com.izkml.trace.core.json;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class JsonUtils{

    private static Json delegate;

    static {
        delegate = JsonFactory.createJson();
    }

    /**
     * 验证是否可用
     * @return
     */
    public static boolean validate(){
        return delegate !=null;
    }

    /**
     * 设置自定义json
     * @param json
     */
    public static void setJson(Json json){
        JsonUtils.delegate = json;
    }

    public static String toJson(Object ob) {
        check();
        return delegate.toJson(ob);
    }

    public static  <T> T parse(String json, Class<T> clazz) {
        check();
        return delegate.parse(json,clazz);
    }

    public static  <T> T parse(String json, Type type) {
        check();
        return delegate.parse(json,type);
    }

    private static void check(){
        if(delegate == null){
            throw new JsonException("not find com.izkml.trace.core.json.Json implement please invoking com.izkml.trace.core.json.JsonUtils.setJson");
        }
    }
}
