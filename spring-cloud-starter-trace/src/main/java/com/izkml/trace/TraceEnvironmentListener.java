package com.izkml.trace;

import com.izkml.trace.core.TraceContext;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;

/**
 * 监听EnvironmentChangeEvent，动态改变trace相关配置属性
 */
public class TraceEnvironmentListener implements ApplicationListener<EnvironmentChangeEvent> {

    private ConfigurableEnvironment environment;
    private TraceContext traceContext;

    public TraceEnvironmentListener(ConfigurableEnvironment environment, TraceContext traceContext){
        this.environment = environment;
        this.traceContext = traceContext;
    }

    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        Map<String,String> map = EnvironmentSupport.getTraceProperties(environment);
        traceContext.refreshProperties(map);
    }
}
