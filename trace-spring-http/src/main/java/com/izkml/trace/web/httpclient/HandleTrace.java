package com.izkml.trace.web.httpclient;

import com.izkml.trace.core.*;
import com.izkml.trace.core.util.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

import static com.izkml.trace.core.TraceConstant.HTTP_CLIENT_SPAN_NAME;

/**
 * httpclient拦截器 拦截请求之前
 */
public class HandleTrace implements HttpRequestInterceptor {

    private SpanHandler spanHandler;

    private TraceContext traceContext = TraceContextFactory.getContext();

    public HandleTrace(SpanHandler spanHandler){
        this.spanHandler = spanHandler;
    }

    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        Span span = traceContext.nextSpan(HTTP_CLIENT_SPAN_NAME);
        String uri = request.getRequestLine().getUri();
        span.setBusinessMark(StringUtils.getSchemeHostPortPath(uri));
        span.setInfoTypeHandler(new RequestInfoTypeHandler(request));
        //将span放入到HttpContext中，供下一个拦截器使用
        context.setAttribute(TraceConstant.ZKML_TRACE,span);

        //将span 放入到request头部
        request.addHeader(TraceConstant.ZKML_TRACE_ID,span.getTraceId());
        request.addHeader(TraceConstant.ZKML_PARENT_SPAN_ID,span.getParentSpanId());
        request.addHeader(TraceConstant.ZKML_SPAN_ID,span.getSpanId());
        spanHandler.handle(span,HandleTrace.class);
    }


}
