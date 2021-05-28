package com.izkml.trace.web.httpclient;

import com.izkml.trace.core.SpanHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 在HttpClientBuilder中注入trace拦截器
 */
public class HttpClientInterceptorInjector implements BeanPostProcessor {

    private SpanHandler spanHandler;

    public HttpClientInterceptorInjector(SpanHandler spanHandler){
        this.spanHandler = spanHandler;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean !=null && bean instanceof HttpClientBuilder){
            HttpClientBuilder httpClientBuilder = (HttpClientBuilder)bean;
            httpClientBuilder.addInterceptorFirst(new HandleTrace(spanHandler));
            httpClientBuilder.addInterceptorLast(new FinishTrace(spanHandler));
        }
        return bean;
    }

}
