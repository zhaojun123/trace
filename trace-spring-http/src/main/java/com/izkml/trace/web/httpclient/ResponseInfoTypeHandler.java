package com.izkml.trace.web.httpclient;

import com.izkml.trace.core.InfoTypeHandler;
import com.izkml.trace.web.HttpTags;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

public class ResponseInfoTypeHandler implements InfoTypeHandler<HttpTags> {

    private HttpResponse response;
    private HttpTags httpTags;

    public ResponseInfoTypeHandler(HttpResponse response,HttpTags httpTags){
        this.response = response;
        this.httpTags = httpTags==null?new HttpTags():httpTags;
    }

    @Override
    public HttpTags simple() {
        StatusLine statusLine = response.getStatusLine();
        if(statusLine!=null){
            httpTags.setStatusCode(statusLine.getStatusCode());
            httpTags.setReason(statusLine.getReasonPhrase());
        }
        return httpTags;
    }

    @Override
    public HttpTags normal() {
        simple();
        return httpTags;
    }

    @Override
    public HttpTags full() {
        normal();
        return httpTags;
    }
}
