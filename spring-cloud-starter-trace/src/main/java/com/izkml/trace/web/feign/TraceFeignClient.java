package com.izkml.trace.web.feign;

import com.izkml.trace.core.*;
import com.izkml.trace.core.util.StringUtils;
import com.izkml.trace.web.HttpTags;
import feign.Client;
import feign.Request;
import feign.Response;

import java.io.IOException;
import java.util.*;

import static com.izkml.trace.core.TraceConstant.FEIGN_SPAN_NAME;

public class TraceFeignClient implements Client {

    private Client delegate;

    private SpanHandler spanHandler;

    private TraceContext traceContext = TraceContextFactory.getContext();

    public TraceFeignClient(Client delegate, SpanHandler spanHandler){
        this.delegate = delegate;
        this.spanHandler = spanHandler;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        Span span = traceContext.nextSpan(FEIGN_SPAN_NAME);
        span.setBusinessMark(StringUtils.getSchemeHostPortPath(request.url()));
        spanHandler.handle(span, TraceFeignClient.class);

        //将span 放入到request头部
        Map<String, Collection<String>> headers = request.headers();
        Map<String, Collection<String>> modifyHeaders = new HashMap<>(headers);
        modifyHeaders.put(TraceConstant.ZKML_TRACE_ID, list(span.getTraceId()));
        modifyHeaders.put(TraceConstant.ZKML_PARENT_SPAN_ID,list(span.getParentSpanId()));
        modifyHeaders.put(TraceConstant.ZKML_SPAN_ID,list(span.getSpanId()));
        Request modifyRequest = Request.create(request.httpMethod(),request.url(),modifyHeaders,request.requestBody());
        Response response = null;
        try {
            return response = this.delegate.execute(modifyRequest, options);
        }catch (Throwable e){
            //记录错误信息
            CurrentSpanContext.setThrowable(e);
            throw  e;
        }finally {
            span.setInfoTypeHandler(new FeignInfoTypeHandler(request,response));
            spanHandler.finish(span, TraceFeignClient.class);
        }
    }

    private List list(Object value){
        List list = new ArrayList();
        list.add(value);
        return list;
    }
}
