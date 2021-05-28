package com.izkml.trace.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * span 处理器
 */
public interface SpanHandler {

    /**
     * 对span进行处理
     * @param span
     * @param caller 调用方class
     */
    void handle(Span span,Class caller);


    /**
     * 追踪完成时的操作
     * @param span
     * @param caller
     */
    void finish(Span span,Class caller);


    abstract class AbstractSpanHandler implements SpanHandler {

        private TraceContext traceContext;
        private List<SpanDecorator> decoratorList = new ArrayList<>();

        protected AbstractSpanHandler(TraceContext traceContext){
            this(traceContext,null);
        }

        protected AbstractSpanHandler(TraceContext traceContext,List<SpanDecorator> customDecoratorList){
            this.traceContext = traceContext;
            if(customDecoratorList!=null){
                decoratorList.addAll(customDecoratorList);
            }
        }

        @Override
        public void handle(Span span, Class caller) {
            for(SpanDecorator spanDecorator:decoratorList){
                spanDecorator.decorate(span);
            }
            CurrentSpanContext.addSpan(span);
            if(infoTypeHandle(span)){
                handleInternal(span,caller);
            }
        }

        @Override
        public void finish(Span span,Class caller) {
            for(SpanDecorator spanDecorator:decoratorList){
                spanDecorator.finish(span);
            }
            CurrentSpanContext.finish(span);
            if(infoTypeHandle(span)){
                finishInternal(span,caller);
            }
            CurrentSpanContext.remove(span);
        }

        /**
         * 根据级别记录不同详细程度的追踪信息
         * @param span
         * @return
         */
        private boolean infoTypeHandle(Span span){
            InfoTypeHandler infoTypeHandler = span.getInfoTypeHandler();
            if(infoTypeHandler == null){
                return false;
            }
            StringBuilder s = new StringBuilder(span.getSpanName())
                    .append(".")
                    .append(TraceConstant.INFO_TYPE);
            String infoTypeValue;
            String defaultInfoTypeName = s.toString();
            if(span.getBusinessMark()!=null){
                s.append(".").append(span.getBusinessMark());
                infoTypeValue = traceContext.getStringProperty(s.toString()
                        ,traceContext.getStringProperty(defaultInfoTypeName,"NORMAL"));
            }else{
                infoTypeValue = traceContext.getStringProperty(defaultInfoTypeName,"NORMAL");
            }
            Object target = infoTypeHandler.handler(infoTypeValue);
            if(target == null){
                return false;
            }
            span.setTags(target);
            return true;
        }

        public TraceContext getTraceContext() {
            return traceContext;
        }

        public abstract void handleInternal(Span span,Class caller);

        public abstract void finishInternal(Span span,Class caller);
    }
}
