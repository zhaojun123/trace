package com.izkml.trace.core.log;

import com.izkml.trace.core.TraceConstant;
import com.izkml.trace.core.Span;
import com.izkml.trace.core.SpanHandler;
import com.izkml.trace.core.TraceContext;

/**
 * 日志处理器
 */
public class LogSpanHandler extends SpanHandler.AbstractSpanHandler {

    private LogPrinter logPrinter;

    public LogSpanHandler(TraceContext context,LogPrinter logPrinter){
        super(context);
        this.logPrinter = logPrinter==null?new DefaultLogPrinter():logPrinter;
    }

    public LogSpanHandler(TraceContext traceContext){
        this(traceContext,null);
    }

    @Override
    public void handleInternal(Span span,Class caller) {

    }

    @Override
    public void finishInternal(Span span,Class caller) {
        String level = getTraceContext().getStringProperty(TraceConstant.LOGGING_TRACE_LEVEL, "INFO");
        String message = getTraceContext().getMessageConverter().convert(span.getTags());
        logPrinter.log(message,level,caller.getName(),null);
    }
}
