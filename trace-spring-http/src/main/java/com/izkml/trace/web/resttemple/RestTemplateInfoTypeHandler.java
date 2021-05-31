package com.izkml.trace.web.resttemple;

import com.izkml.trace.core.InfoTypeHandler;
import com.izkml.trace.web.HttpTags;
import org.apache.http.RequestLine;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestTemplateInfoTypeHandler implements InfoTypeHandler<HttpTags> {

    private HttpTags httpTags;

    private HttpRequest request;

    private ClientHttpResponse response;

    private byte[] body;

    public RestTemplateInfoTypeHandler(HttpRequest request, ClientHttpResponse response,byte[] body){
        this.request = request;
        this.response = response;
        this.httpTags = new HttpTags();
        this.body = body;
    }

    @Override
    public HttpTags simple() {
        URI uri = request.getURI();
        httpTags.setUrl(uri.toString());
        httpTags.setMethod(request.getMethod().name());
        if(response!=null){
            try {
                httpTags.setStatusCode(response.getRawStatusCode());
                httpTags.setReason(response.getStatusText());
            } catch (IOException e) {
            }
        }
        return httpTags;
    }

    @Override
    public HttpTags normal() {
        simple();
        HttpHeaders httpHeaders = request.getHeaders();
        if(body == null || httpHeaders == null){
            return httpTags;
        }
        MediaType contentType = httpHeaders.getContentType();
        if(contentType == null){
            return httpTags;
        }
        //当头部是 application/json 或者是 application/x-www-form-urlencoded时读取body的内容
        if(contentType.equals(MediaType.APPLICATION_JSON)
                || contentType.equals(MediaType.APPLICATION_JSON_UTF8)
                || contentType.equals(MediaType.APPLICATION_FORM_URLENCODED)){
            Charset charset = contentType.getCharset()==null?StandardCharsets.UTF_8:contentType.getCharset();
            httpTags.setRequestBody(new String(body,charset));
        }
        return httpTags;
    }

    @Override
    public HttpTags full() {
        normal();
        HttpHeaders httpHeaders = request.getHeaders();
        if(httpHeaders==null){
            return httpTags;
        }
        Map<String,String> headers = new HashMap<>();
        httpHeaders.entrySet().forEach(entry->{
            String name = entry.getKey();
            List<String> values = entry.getValue();
            if(values==null || values.isEmpty()){
                headers.put(name,null);
            }else{
                headers.put(name,values.get(0));
            }
        }
        );
        return httpTags;
    }
}
