package com.izkml.trace.core;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Properties工具类，可以从jvm system properties中读取参数
 */
public class PropertiesSupport {

    private volatile Map<String,String> normal = new HashMap<>();

    private volatile Map<String,String> env = new HashMap<>();

    public PropertiesSupport(Map<String,String> properties){
        refreshNormal(properties);
        refreshEnv();
    }

    public synchronized void refreshNormal(Map<String,String> properties){
        if(properties!=null){
            Map<String,String> newNormal = new HashMap<>();
            newNormal.putAll(properties);
            normal = newNormal;
        }
    }

    /**
     * 支持重加载，谨慎使用
     */
    public synchronized void refreshEnv(){
        Map<String,String> newEnv = new ConcurrentHashMap<>();

        Properties properties;
        try {
            properties = System.getProperties();
            final Object[] keySet;
            synchronized (properties) {
                keySet = properties.keySet().toArray();
            }
            for (final Object key : keySet) {
                final String keyStr = Objects.toString(key, null);
                if(keyStr!=null && keyStr.startsWith("trace.")){
                    newEnv.put(keyStr,properties.getProperty(keyStr));
                }
            }
            this.env = newEnv;
        } catch (final SecurityException e) {

        }
    }

    public boolean hasProperty(final String name) {
        return env.containsKey(name) || normal.containsKey(name);
    }


    public String getStringProperty(final String name) {
        String value = env.get(name);
        if(value == null){
            value = normal.get(name);
        }
        return value;
    }

}
