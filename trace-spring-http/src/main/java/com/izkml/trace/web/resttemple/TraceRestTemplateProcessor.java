package com.izkml.trace.web.resttemple;

import com.izkml.trace.core.SpanHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class TraceRestTemplateProcessor implements BeanPostProcessor {

    private SpanHandler spanHandler;

    public TraceRestTemplateProcessor(SpanHandler spanHandler){
        this.spanHandler = spanHandler;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean!=null && bean instanceof RestTemplate){
            RestTemplate restTemplate = (RestTemplate)bean;
            List<ClientHttpRequestInterceptor> interceptors =  restTemplate.getInterceptors();
            interceptors.add(0,new TraceClientHttpRequestInterceptor(spanHandler));
            restTemplate.setInterceptors(interceptors);
        }
        return bean;
    }

}
