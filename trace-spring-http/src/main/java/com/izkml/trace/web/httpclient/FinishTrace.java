package com.izkml.trace.web.httpclient;

import com.izkml.trace.core.*;
import com.izkml.trace.web.HttpTags;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.StatusLine;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * httpclient拦截器 拦截返回之后
 */
public class FinishTrace implements HttpResponseInterceptor {

    private SpanHandler spanHandler;

    public FinishTrace(SpanHandler spanHandler){
        this.spanHandler = spanHandler;
    }

    @Override
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        Span span = (Span)context.getAttribute(TraceConstant.ZKML_TRACE);
        if(span!=null){
            span.setInfoTypeHandler(new ResponseInfoTypeHandler(response,(HttpTags)span.getTags()));
            spanHandler.finish(span,FinishTrace.class);
        }
    }
}
