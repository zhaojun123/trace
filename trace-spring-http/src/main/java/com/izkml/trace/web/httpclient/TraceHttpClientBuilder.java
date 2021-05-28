package com.izkml.trace.web.httpclient;

import com.izkml.trace.core.log.LogSpanHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * 继承HttpClientBuilder 新增trace拦截器
 */
public class TraceHttpClientBuilder extends HttpClientBuilder {

    private LogSpanHandler logSpanHandler;

    public TraceHttpClientBuilder(LogSpanHandler logSpanHandler){
        this.logSpanHandler = logSpanHandler;
    }

    @Override
    public CloseableHttpClient build(){
        super.addInterceptorFirst(new HandleTrace(logSpanHandler));
        super.addInterceptorLast(new FinishTrace(logSpanHandler));
        return super.build();
    }

}
