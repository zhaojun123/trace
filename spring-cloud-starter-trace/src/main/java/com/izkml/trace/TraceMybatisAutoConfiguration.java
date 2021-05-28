package com.izkml.trace;

import com.izkml.trace.core.SpanHandler;
import com.izkml.trace.core.TraceContext;
import com.izkml.trace.mybatis.TraceMybatisInterceptor;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(MybatisAutoConfiguration.class)
@AutoConfigureBefore(MybatisAutoConfiguration.class)
@AutoConfigureAfter(TraceAutoConfiguration.class)
@ConditionalOnBean(TraceContext.class)
public class TraceMybatisAutoConfiguration {

    @Bean
    @ConditionalOnProperty(value = "trace.mybatis.enable",havingValue = "true",matchIfMissing = true)
    public TraceMybatisInterceptor traceMybatisInterceptor(SpanHandler spanHandler){
        return new TraceMybatisInterceptor(spanHandler);
    }

}
