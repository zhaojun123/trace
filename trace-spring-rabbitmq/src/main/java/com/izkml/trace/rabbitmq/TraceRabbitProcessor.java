package com.izkml.trace.rabbitmq;

import com.izkml.trace.core.SpanHandler;
import org.aopalliance.aop.Advice;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.config.AbstractRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 *  将rabbitTemplate 替换成TraceRabbitTemplate
 */
public class TraceRabbitProcessor implements BeanPostProcessor {

    private SpanHandler spanHandler;

    public TraceRabbitProcessor(SpanHandler spanHandler){
        this.spanHandler = spanHandler;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean==null){
            return bean;
        }
        if(bean instanceof RabbitTemplate){
            RabbitTemplate rabbitTemplate = (RabbitTemplate)bean;
            addMessageProcessor(rabbitTemplate);
            return proxy(bean);
        }
        if(bean instanceof AbstractRabbitListenerContainerFactory){
            AbstractRabbitListenerContainerFactory factory = (AbstractRabbitListenerContainerFactory)bean;
            addAdvice(factory);
        }
        return bean;
    }


    private void addAdvice(AbstractRabbitListenerContainerFactory factory){
        Field field = null;
        try {
            field = factory.getClass().getDeclaredField("adviceChain");
        } catch (NoSuchFieldException e) {
            Class superClass = factory.getClass().getSuperclass();
            while(superClass!=null && superClass!=AbstractRabbitListenerContainerFactory.class){
                superClass = factory.getClass().getSuperclass();
            }
            if(superClass!=null){
                try {
                    field = superClass.getDeclaredField("adviceChain");
                } catch (NoSuchFieldException ex) {
                }
            }
        }
        if(field!=null){
            try {
            field.setAccessible(true);
            Advice[] advice = (Advice[])field.get(factory);
            if(advice == null){
                advice = new Advice[]{new TraceRabbitListenerAdvice(spanHandler)};
            }else{
                Advice[] newAdvice = new Advice[advice.length+1];
                System.arraycopy(advice, 0, newAdvice, 0, advice.length);
                newAdvice[newAdvice.length] =  new TraceRabbitListenerAdvice(spanHandler);
                advice = newAdvice;
            }
                field.set(factory,advice);
            } catch (IllegalAccessException e) {
            }
        }
    }

    private Object proxy(Object target){
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.addAdvice(new TraceRabbitInterceptor(spanHandler));
        proxyFactory.setTarget(target);
        return proxyFactory.getProxy();
    }

    /**
     * 2.1.4版本后才有addBeforePublishPostProcessors方法，这里通过反射解决
     * @param rabbitTemplate
     */
    private void addMessageProcessor(RabbitTemplate rabbitTemplate){
        try {
            Field beforePublishPostProcessorsField = rabbitTemplate.getClass().getDeclaredField("beforePublishPostProcessors");
            beforePublishPostProcessorsField.setAccessible(true);
            Collection<MessagePostProcessor> beforePublishPostProcessors =(Collection)beforePublishPostProcessorsField.get(rabbitTemplate);
            if(beforePublishPostProcessors==null){
                rabbitTemplate.setBeforePublishPostProcessors(new TraceMessagePostProcessor());
                return;
            }
            for(MessagePostProcessor messagePostProcessor:beforePublishPostProcessors){
                if(messagePostProcessor instanceof TraceMessagePostProcessor){
                    return;
                }
            }
            beforePublishPostProcessors.add(new TraceMessagePostProcessor());
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
    }

}
