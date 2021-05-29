package com.izkml.trace;

import org.apache.logging.log4j.util.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentSupport {

    /**
     * 从 environment中获取trace相关的配置属性
     * @param environment
     * @return
     */
    public static Map<String,String> getTraceProperties(ConfigurableEnvironment environment){
        Map<String,String> propertiesMap = new HashMap<>();
        MutablePropertySources propertySources = environment.getPropertySources();
        propertySources.forEach(propertySource -> {
            //如果是可以遍历的PropertySource 则遍历存储的属性
            if(propertySource instanceof EnumerablePropertySource){
                EnumerablePropertySource enumerablePropertySource = (EnumerablePropertySource)propertySource;
                String[] propertyNames =  enumerablePropertySource.getPropertyNames();
                if(propertyNames!=null){
                    for(String propertyName:propertyNames){
                        //这里一定要判断有没有重复的key 以第一个key的值为准
                        //否则配置中心发布的key将会被本地值覆盖
                        if(propertyName!=null && propertyName.startsWith("trace.") && !propertiesMap.containsKey(propertyName)){
                            Object value = enumerablePropertySource.getProperty(propertyName);
                            if(value==null){
                                propertiesMap.put(propertyName,null);
                            }else{
                                propertiesMap.put(propertyName,value.toString());
                            }
                        }
                    }
                }
            }
        });
        return propertiesMap;
    }

}
