package com.izkml.trace.core;

import java.util.Map;

/**
 * 配置类
 */
public class TraceConfiguration {

    /**
     * 转换器，将对象转换成文本
     */
    private MessageConverter messageConverter;
    /**
     * 配置参数，可以通过该属性传递配置参数，也可以通过System.setProperty配置
     * 如果属性名相同 后者覆盖前者
     */
    private Map<String,String> propertiesMap;

    public MessageConverter getMessageConverter() {
        return messageConverter;
    }

    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    public Map<String, String> getPropertiesMap() {
        return propertiesMap;
    }

    public void setPropertiesMap(Map<String, String> propertiesMap) {
        this.propertiesMap = propertiesMap;
    }
}
