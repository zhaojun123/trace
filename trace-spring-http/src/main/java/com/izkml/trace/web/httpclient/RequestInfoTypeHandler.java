package com.izkml.trace.web.httpclient;

import com.izkml.trace.core.InfoTypeHandler;
import com.izkml.trace.core.util.IOUtils;
import com.izkml.trace.web.HttpTags;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.RequestLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * request请求前组装HttpTags
 */
public class RequestInfoTypeHandler implements InfoTypeHandler<HttpTags> {

    private HttpTags httpTags;

    private HttpRequest request;

    public RequestInfoTypeHandler(HttpRequest request){
        this.request = request;
        this.httpTags = new HttpTags();
    }

    @Override
    public HttpTags simple() {
        RequestLine requestLine = request.getRequestLine();
        httpTags.setUrl(requestLine.getUri());
        httpTags.setMethod(requestLine.getMethod());
        return httpTags;
    }

    @Override
    public HttpTags normal() {
        simple();
        if(request instanceof HttpEntityEnclosingRequestBase){
            HttpEntityEnclosingRequestBase http = (HttpEntityEnclosingRequestBase)request;
            HttpEntity httpEntity = http.getEntity();
            if(httpEntity!=null && httpEntity instanceof StringEntity){
                StringEntity stringEntity = (StringEntity)httpEntity;
                //默认用UTF-8解析
                try {
                    httpTags.setRequestBody(IOUtils.convertStreamToString(stringEntity.getContent(),"utf-8"));
                } catch (IOException e) {
                }
            }
        }
        return httpTags;
    }

    @Override
    public HttpTags full() {
        normal();
        Header[] headers = request.getAllHeaders();
        if(headers!=null){
            Map<String,String> map = new HashMap<>();
            for(Header header:headers){
                map.put(header.getName(),header.getValue());
            }
            httpTags.setHeaders(map);
        }
        return httpTags;
    }
}
