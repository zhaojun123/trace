package com.izkml.trace.rabbitmq;

import com.izkml.trace.core.CurrentSpanContext;
import com.izkml.trace.core.Span;
import com.izkml.trace.core.TraceConstant;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;

/**
 * 将span信息放入到Message头部
 */
public class TraceMessagePostProcessor implements MessagePostProcessor {

    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        Span span = CurrentSpanContext.getCurrentSpan();
        if(span!=null){
            //将span 放入到message的头部
            MessageProperties properties = message.getMessageProperties();
            if(properties == null){
                properties = new MessageProperties();
                message = new Message(message.getBody(),properties);
            }
            properties.setHeader(TraceConstant.ZKML_TRACE_ID,span.getTraceId());
            properties.setHeader(TraceConstant.ZKML_PARENT_SPAN_ID,span.getParentSpanId());
            properties.setHeader(TraceConstant.ZKML_SPAN_ID,span.getSpanId());
            span.setInfoTypeHandler(new RabbitInfoTypeHandler(message));
        }
        return message;
    }
}
