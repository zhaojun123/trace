package com.izkml.trace.rabbitmq;

import com.izkml.trace.core.*;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;

import java.util.Map;

import static com.izkml.trace.core.TraceConstant.RABBIT_RECEIVE_SPAN_NAME;

/**
 * 拦截 {@link AbstractMessageListenerContainer.ContainerDelegate#invokeListener(com.rabbitmq.client.Channel, org.springframework.amqp.core.Message)}
 */
public class TraceRabbitListenerAdvice implements MethodInterceptor {

    private TraceContext traceContext = TraceContextFactory.getContext();

    private SpanHandler spanHandler;

    public TraceRabbitListenerAdvice(SpanHandler spanHandler){
        this.spanHandler = spanHandler;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Message message = (Message)invocation.getArguments()[1];
        Span span = createSpan(message);
        spanHandler.handle(span, TraceRabbitListenerAdvice.class);
        try{
            return invocation.proceed();
        }catch (Throwable e){
            //记录错误信息
            CurrentSpanContext.setThrowable(e);
            throw  e;
        }finally {
            span.setInfoTypeHandler(new RabbitInfoTypeHandler(message));
            spanHandler.finish(span,TraceRabbitListenerAdvice.class);
        }
    }

    private Span createSpan(Message message){
        if(message == null || message.getMessageProperties() == null){
            return Span.create(traceContext.getApplicationName(),RABBIT_RECEIVE_SPAN_NAME);
        }
        MessageProperties messageProperties = message.getMessageProperties();
        Map<String, Object> headers =  messageProperties.getHeaders();
        if(!headers.containsKey(TraceConstant.ZKML_TRACE_ID)){
            return Span.create(traceContext.getApplicationName(),RABBIT_RECEIVE_SPAN_NAME);
        }
        return new Span.Builder()
                .traceId((String)headers.get(TraceConstant.ZKML_TRACE_ID))
                .parentSpanId((String)headers.get(TraceConstant.ZKML_SPAN_ID))
                .applicationName(traceContext.getApplicationName())
                .spanName(RABBIT_RECEIVE_SPAN_NAME)
                .build();
    }

}
