package com.izkml.trace.web.feign;

import com.izkml.trace.core.Span;
import com.izkml.trace.core.SpanHandler;
import feign.Client;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;

/**
 * 拦截feign的client 用TraceFeignClient代理
 */
public class TraceFeignClientProcessor implements BeanPostProcessor {

    private SpanHandler spanHandler;

    public TraceFeignClientProcessor(SpanHandler spanHandler){
        this.spanHandler = spanHandler;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean!=null && bean instanceof Client){
            return new TraceFeignClient((Client)bean,spanHandler);
        }
        return bean;
    }
}
