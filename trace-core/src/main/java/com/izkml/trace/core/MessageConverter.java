package com.izkml.trace.core;

import com.izkml.trace.core.json.JsonUtils;

import java.util.Objects;

/**
 * 转换器，将对象转换成文本
 */
public interface MessageConverter {

    /**
     * 转换
     * @param object
     * @return
     */
    String convert(Object object);

    
    class DefaultMessageConverter implements MessageConverter{

        @Override
        public String convert(Object object) {
            if(JsonUtils.validate()){
                return JsonUtils.toJson(object);
            }
            return Objects.toString(object,null);
        }
    }
}
