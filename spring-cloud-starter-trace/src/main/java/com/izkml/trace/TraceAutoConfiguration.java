package com.izkml.trace;

import com.izkml.trace.core.MessageConverter;
import com.izkml.trace.core.TraceConfiguration;
import com.izkml.trace.core.TraceContext;
import com.izkml.trace.core.TraceContextFactory;
import com.izkml.trace.core.log.DefaultLogPrinter;
import com.izkml.trace.core.log.LogPrinter;
import com.izkml.trace.core.log.LogSpanHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;

/**
 * 自动装载TraceContext
 */
@Configuration
@ConditionalOnProperty(value = "trace.enable",havingValue = "true",matchIfMissing = true)
public class TraceAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    TraceConfiguration traceConfiguration(@Autowired(required = false)MessageConverter messageConverter
            ,ConfigurableEnvironment environment){
        Map<String,String> propertiesMap = EnvironmentSupport.getTraceProperties(environment);
        TraceConfiguration traceConfiguration = new TraceConfiguration();
        traceConfiguration.setMessageConverter(messageConverter);
        traceConfiguration.setPropertiesMap(propertiesMap);
        return traceConfiguration;
    }

    @Bean
    @ConditionalOnMissingBean
    TraceContext traceContext(TraceConfiguration traceConfiguration){
        return TraceContextFactory.createContext(traceConfiguration);
    }

    @Bean
    @ConditionalOnMissingBean
    LogPrinter logPrinter(){
        return new DefaultLogPrinter();
    }

    @Bean
    @ConditionalOnMissingBean
    LogSpanHandler logSpanHandler(TraceContext traceContext,LogPrinter logPrinter){
        return new LogSpanHandler(traceContext,logPrinter);
    }

    @Bean
    @ConditionalOnMissingBean
    TraceEnvironmentListener traceEnvironmentListener(TraceContext traceContext,ConfigurableEnvironment environment){
        return new TraceEnvironmentListener(environment,traceContext);
    }
}
