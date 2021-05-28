package com.izkml.trace.web.feign;

import com.izkml.trace.core.InfoTypeHandler;
import com.izkml.trace.web.HttpTags;
import feign.Request;
import feign.Response;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FeignInfoTypeHandler implements InfoTypeHandler<HttpTags> {

    private Request request;
    private Response response;
    private HttpTags httpTags;

    public FeignInfoTypeHandler(Request request,Response response){
        this.request = request;
        this.response = response;
        this.httpTags = new HttpTags();
    }

    @Override
    public HttpTags simple() {
        httpTags.setMethod(request.httpMethod().name());
        httpTags.setUrl(request.url());
        if(response!=null){
            httpTags.setReason(response.reason());
            httpTags.setStatusCode(response.status());
        }
        return httpTags;
    }

    @Override
    public HttpTags normal() {
        simple();
        Request.Body body = request.requestBody();
        if(body!=null && body.asBytes()!=null){
            httpTags.setRequestBody(body.asString());
        }
        return httpTags;
    }

    @Override
    public HttpTags full() {
        normal();
        Map<String, Collection<String>> headers = request.headers();
        if(headers!=null){
            Map<String,String> map = new HashMap<>();
            headers.forEach((key,values)->{
                if(values!=null && !values.isEmpty()){
                    map.put(key,values.iterator().next());
                }
            });
            httpTags.setHeaders(map);
        }
        return httpTags;
    }
}
