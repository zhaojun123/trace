package com.izkml.trace;

import com.izkml.trace.core.SpanHandler;
import com.izkml.trace.core.TraceContext;
import com.izkml.trace.core.log.LogSpanHandler;
import com.izkml.trace.web.feign.TraceFeignClientProcessor;
import com.izkml.trace.web.httpclient.HttpClientInterceptorInjector;
import com.izkml.trace.web.TraceClientHttpRequestInterceptor;
import com.izkml.trace.web.servlet.TraceFilter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.servlet.DispatcherType;

/**
 * httpclient、restTemplate 等http请求 trace拦截器自动装载
 */
@Configuration
@AutoConfigureAfter(TraceAutoConfiguration.class)
@ConditionalOnBean(TraceContext.class)
public class TraceHttpAutoConfiguration {

    static final int TRACE_FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE + 5;

    @Bean
    @ConditionalOnProperty(value = "trace.httpclient.enable",havingValue = "true")
    @ConditionalOnClass(name = "org.apache.http.impl.client.HttpClientBuilder")
    HttpClientInterceptorInjector httpClientInterceptorInjector(SpanHandler spanHandler) {
        return new HttpClientInterceptorInjector(spanHandler);
    }

    @Bean
    @ConditionalOnProperty(value = "trace.restTemplate.enable",havingValue = "true",matchIfMissing = true)
    @ConditionalOnClass(name="org.springframework.http.client.ClientHttpRequestInterceptor")
    TraceClientHttpRequestInterceptor traceClientHttpRequestInterceptor(SpanHandler spanHandler){
        return new TraceClientHttpRequestInterceptor(spanHandler);
    }

    @Bean
    @ConditionalOnClass(name = "feign.Client")
    @ConditionalOnProperty(value = "trace.feign.enable",havingValue = "true",matchIfMissing = true)
    TraceFeignClientProcessor traceFeignClientProcessor(SpanHandler spanHandler){
        return new TraceFeignClientProcessor(spanHandler);
    }

    @Bean
    @ConditionalOnProperty(value = "trace.servlet.enable",havingValue = "true",matchIfMissing = true)
    public FilterRegistrationBean traceFilter(LogSpanHandler logSpanHandler){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(
                new TraceFilter(logSpanHandler));
        filterRegistrationBean.setDispatcherTypes(DispatcherType.ASYNC,
                DispatcherType.ERROR, DispatcherType.FORWARD, DispatcherType.INCLUDE,
                DispatcherType.REQUEST);
        filterRegistrationBean.setOrder(TRACE_FILTER_ORDER);
        return filterRegistrationBean;
    }

}
