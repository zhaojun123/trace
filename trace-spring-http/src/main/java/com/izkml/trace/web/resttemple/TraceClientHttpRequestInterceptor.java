package com.izkml.trace.web.resttemple;

import com.izkml.trace.core.*;
import com.izkml.trace.core.util.StringUtils;
import com.izkml.trace.web.HttpTags;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.InterceptingHttpAccessor;

import java.io.IOException;

/**
 * RestTemplate 拦截器
 * 参考{@link InterceptingHttpAccessor}
 */
public class TraceClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private TraceContext traceContext = TraceContextFactory.getContext();

    private SpanHandler spanHandler;

    public TraceClientHttpRequestInterceptor(SpanHandler spanHandler){
        this.spanHandler = spanHandler;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        Span span = traceContext.nextSpan(TraceConstant.REST_TEMPLATE_SPAN_NAME);
        span.setBusinessMark(StringUtils.getSchemeHostPortPath(request.getURI()));

        //将span 放入到request头部
        HttpHeaders httpHeaders = request.getHeaders();
        httpHeaders.add(TraceConstant.ZKML_TRACE_ID,span.getTraceId());
        httpHeaders.add(TraceConstant.ZKML_PARENT_SPAN_ID,span.getParentSpanId());
        httpHeaders.add(TraceConstant.ZKML_SPAN_ID,span.getSpanId());

        spanHandler.handle(span, TraceClientHttpRequestInterceptor.class);

        ClientHttpResponse response = null;
        try {
            return response = execution.execute(request, body);
        }catch (Throwable e){
            //记录错误信息
            CurrentSpanContext.setThrowable(e);
            throw  e;
        } finally {
            span.setInfoTypeHandler(new RestTemplateInfoTypeHandler(request,response,body));
            spanHandler.finish(span,TraceClientHttpRequestInterceptor.class);
        }
    }
}
