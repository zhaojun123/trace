package com.izkml.trace.web.servlet;

import com.izkml.trace.core.*;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.izkml.trace.core.TraceConstant.SERVLET_SPAN_NAME;

/**
 * servlet 追踪
 */
public class TraceFilter implements Filter {

    private TraceContext traceContext = TraceContextFactory.getContext();

    private SpanHandler spanHandler;

    public TraceFilter(SpanHandler spanHandler){
        this.spanHandler = spanHandler;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ContentCachingRequestWrapper httpRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
        Span span = createSpan(httpRequest);
        spanHandler.handle(span, TraceFilter.class);
        try {
            chain.doFilter(request, response);
        }catch (Throwable e){
            //记录错误信息
            CurrentSpanContext.setThrowable(e);
            throw  e;
        }finally {
            span.setInfoTypeHandler(new ServletInfoTypeHandler(httpRequest,(HttpServletResponse)response));
            spanHandler.finish(span,TraceFilter.class);
        }

    }



    @Override
    public void destroy() {

    }

    private Span createSpan(ContentCachingRequestWrapper httpRequest){
        Span span;
        String traceId = httpRequest.getHeader(TraceConstant.ZKML_TRACE_ID);
        if(traceId!=null){
            span = new Span.Builder()
                    .traceId(traceId)
                    .parentSpanId(httpRequest.getHeader(TraceConstant.ZKML_SPAN_ID))
                    .applicationName(traceContext.getApplicationName())
                    .spanName(SERVLET_SPAN_NAME)
                    .build();
        }else{
            span = Span.create(traceContext.getApplicationName(),SERVLET_SPAN_NAME);
        }
        span.setBusinessMark(httpRequest.getRequestURI());
        return span;
    }

}
