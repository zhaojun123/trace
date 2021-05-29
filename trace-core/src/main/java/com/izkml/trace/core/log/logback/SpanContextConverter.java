package com.izkml.trace.core.log.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;
import com.izkml.trace.core.CurrentSpanContext;
import com.izkml.trace.core.Span;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.izkml.trace.core.TraceConstant.*;

public class SpanContextConverter extends CompositeConverter<ILoggingEvent> {

    @Override
    protected String transform(ILoggingEvent event, String in) {
        Map<String,String> attrMap = new HashMap<>();
        Span current = CurrentSpanContext.getCurrentSpan();
        if(current == null && event.getThrowableProxy()!=null){
            current = CurrentSpanContext.getThrowableSpan();
        }
        if(current!=null){
            attrMap.put(ZKML_APPLICATION_NAME,current.getApplicationName());
            attrMap.put(ZKML_TRACE_ID,current.getTraceId());
            attrMap.put(ZKML_PARENT_SPAN_ID,current.getParentSpanId());
            attrMap.put(ZKML_SPAN_ID,current.getSpanId());
            attrMap.put(ZKML_SPAN_NAME,current.getSpanName());
            attrMap.put(ZKML_FINISH_TIME, Objects.toString(current.getFinishTime(),null));
            attrMap.put(ZKML_SPAN_TYPE, current.getSpanType()==null?null:current.getSpanType().name());
        }
        in = format(in,attrMap);
        return in;
    }

    /**
     * 将 #{}中的属性名替换成相应的属性值
     * @param str
     * @param attrMap
     * @return
     */
    private String format(String str, Map<String,String> attrMap){
        if(str == null){
            return str;
        }
        StringBuilder result = new StringBuilder();
        char[] charArray = str.toCharArray();
        int attrStart = 0;
        boolean isAttr = false;
        for(int i=0;i<charArray.length;i++){
            if(charArray[i] == '#' && i<charArray.length && charArray[i+1] == '{'){
                isAttr = true;
                i++;
                attrStart = i+1;
                continue;
            }
            if(charArray[i] == '}' &&  isAttr == true){
                int attrEnd = i-1;
                if(attrEnd>= attrStart){
                    String attrName = new String(charArray,attrStart,attrEnd-attrStart+1);
                    String attrValue = attrMap.get(attrName);
                    if(attrValue !=null){
                        result.append(attrValue);
                    }
                }
                isAttr = false;
                continue;
            }
            if(isAttr){
                continue;
            }
            result.append(charArray[i]);
        }
        return result.toString();
    }
}
