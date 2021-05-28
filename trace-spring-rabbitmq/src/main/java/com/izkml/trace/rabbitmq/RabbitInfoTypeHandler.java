package com.izkml.trace.rabbitmq;

import com.izkml.trace.core.InfoTypeHandler;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.utils.SerializationUtils;
import org.springframework.util.ClassUtils;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class RabbitInfoTypeHandler implements InfoTypeHandler<RabbitMqTags>{

    private Message message;

    private RabbitMqTags rabbitMqTags;

    private static final Set<String> whiteListPatterns =
            new LinkedHashSet<>(Arrays.asList("java.util.*", "java.lang.*"));

    public RabbitInfoTypeHandler(Message message){
        this.message = message;
        this.rabbitMqTags = new RabbitMqTags();
    }

    @Override
    public RabbitMqTags simple() {
        rabbitMqTags.setMessage(getBodyContentAsString());
        return  rabbitMqTags;
    }

    @Override
    public RabbitMqTags normal() {
        return simple();
    }

    @Override
    public RabbitMqTags full() {
        normal();
        MessageProperties messageProperties = message.getMessageProperties();
        if(messageProperties!=null){
            rabbitMqTags.setHeaders(messageProperties.toString());
        }
        return rabbitMqTags;
    }

    private String getBodyContentAsString() {
        if (this.message == null || message.getBody() == null) {
            return null;
        }
        try {
            MessageProperties messageProperties = message.getMessageProperties();
            String contentType = (messageProperties != null) ? messageProperties.getContentType() : null;
            if (MessageProperties.CONTENT_TYPE_SERIALIZED_OBJECT.equals(contentType)) {
                return SerializationUtils.deserialize(new ByteArrayInputStream(message.getBody()), whiteListPatterns,
                        ClassUtils.getDefaultClassLoader()).toString();
            }
            if (MessageProperties.CONTENT_TYPE_TEXT_PLAIN.equals(contentType)
                    || MessageProperties.CONTENT_TYPE_JSON.equals(contentType)
                    || MessageProperties.CONTENT_TYPE_JSON_ALT.equals(contentType)
                    || MessageProperties.CONTENT_TYPE_XML.equals(contentType)) {
                return new String(message.getBody(), Charset.defaultCharset().name());
            }
        }
        catch (Exception e) {
            // ignore
        }
        // Comes out as '[B@....b' (so harmless)
        return message.getBody().toString();
    }
}
