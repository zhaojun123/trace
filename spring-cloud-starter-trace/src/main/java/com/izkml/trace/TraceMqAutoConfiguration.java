package com.izkml.trace;


import com.izkml.trace.core.SpanHandler;
import com.izkml.trace.core.TraceContext;
import com.izkml.trace.rabbitmq.TraceRabbitProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(TraceAutoConfiguration.class)
@ConditionalOnBean(TraceContext.class)
@ConditionalOnProperty(value = "trace.mq.enable",havingValue = "true",matchIfMissing = true)
public class TraceMqAutoConfiguration {

    @Bean
    @ConditionalOnClass(name = "org.springframework.amqp.rabbit.core.RabbitTemplate")
    public TraceRabbitProcessor traceRabbitProcessor(SpanHandler spanHandler){
        return new TraceRabbitProcessor(spanHandler);
    }

}
