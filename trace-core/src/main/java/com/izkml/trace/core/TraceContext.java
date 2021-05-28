package com.izkml.trace.core;


import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 上下文环境
 */
public class TraceContext{

    /**
     * 是否已经初始化
     */
    private boolean initialized = false;

    /**
     * 项目名称
     */
    private String applicationName = null;

    public String getApplicationName() {
        return applicationName;
    }

    /**
     * 消息转换器
     */
    private MessageConverter messageConverter;

    public MessageConverter getMessageConverter() {
        return messageConverter;
    }

    private PropertiesSupport propertiesSupport;

    /**
     * 通用参数格式是trace.开头
     */
    private static final String prefix = "trace.";


    private Map<String,String> propertiesMap = new HashMap<>();

    private Map<String,String> customPropertiesMap;

    private RefreshHandle refreshHandle;

    TraceContext(TraceConfiguration traceConfiguration){
        if(traceConfiguration!=null){
            this.messageConverter = traceConfiguration.getMessageConverter();
            this.customPropertiesMap = traceConfiguration.getPropertiesMap();
        }
    }

    public synchronized void  init(){
        if(initialized){
            return;
        }
        defaultProperties(propertiesMap);
        if(customPropertiesMap!=null){
            propertiesMap.putAll(customPropertiesMap);
        }
        this.propertiesSupport = new PropertiesSupport(propertiesMap);
        applicationName =  getStringProperty(TraceConstant.APPLICATION_NAME,null);
        //可以自动刷新参数
        boolean propertiesAutoRefresh = getBooleanProperty(TraceConstant.PROPERTIES_AUTO_REFRESH, true);
        if(propertiesAutoRefresh){
            refreshHandle = new RefreshHandle();
            refreshHandle.refresh();
        }
        if(messageConverter == null){
            messageConverter = new MessageConverter.DefaultMessageConverter();
        }
        initialized = true;
    }

    /**
     * 获取当前节点的span，有可能为null
     * @return
     */
    public Span getCurrentSpan(){
        return CurrentSpanContext.getCurrentSpan();
    }

    /**
     * 通过当前节点的span创建下一个span
     * @return
     */
    public Span nextSpan(String spanName){
        Span span = getCurrentSpan();
        if(span == null){
            span = Span.create(getApplicationName(),spanName);
        }else{
            span = span.createNextSpan(spanName);
        }
        return span;
    }

    /**
     * 初始化一些默认属性
     * @param propertiesMap
     */
    protected void defaultProperties(Map<String,String> propertiesMap){

    }
    private String getPropertyName(String name){
        return prefix+name;
    }


    public boolean getBooleanProperty(final String name) {
        return getBooleanProperty(name, false);
    }


    public boolean getBooleanProperty(final String name, final boolean defaultValue) {
        final String prop = getStringProperty(name);
        return prop == null ? defaultValue : "true".equalsIgnoreCase(prop);
    }


    public double getDoubleProperty(final String name, final double defaultValue) {
        final String prop = getStringProperty(name);
        if (prop != null) {
            try {
                return Double.parseDouble(prop);
            } catch (final Exception ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public int getIntegerProperty(final String name, final int defaultValue) {
        final String prop = getStringProperty(name);
        if (prop != null) {
            try {
                return Integer.parseInt(prop);
            } catch (final Exception ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }


    public long getLongProperty(final String name, final long defaultValue) {
        final String prop = getStringProperty(name);
        if (prop != null) {
            try {
                return Long.parseLong(prop);
            } catch (final Exception ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }


    public String getStringProperty(final String name) {
        String propertyName = getPropertyName(name);
        return propertiesSupport.getStringProperty(propertyName);
    }


    public String getStringProperty(final String name, final String defaultValue) {
        final String prop = getStringProperty(name);
        return (prop == null) ? defaultValue : prop;
    }

    @PreDestroy
    public void close(){
        if(refreshHandle!=null){
            refreshHandle.stop();
        }
    }

    private class RefreshHandle{


        ScheduledExecutorService scheduledExecutorService;

        RefreshHandle(){
            scheduledExecutorService = Executors.newScheduledThreadPool(1);
            Runtime.getRuntime().addShutdownHook(new Thread(()->{
                stop();
            }));
        }

        public void refresh(){
            scheduledExecutorService.scheduleAtFixedRate(()->{
                propertiesSupport.refreshEnv();
            },10,10, TimeUnit.SECONDS);
        }

        public void stop(){
            scheduledExecutorService.shutdownNow();
        }
    }

}
