package com.izkml.trace.web.servlet;

import com.izkml.trace.core.InfoTypeHandler;
import com.izkml.trace.web.HttpTags;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 组装HttpTags
 */
public class ServletInfoTypeHandler implements InfoTypeHandler<HttpTags> {

    private ContentCachingRequestWrapper request;
    private HttpServletResponse response;
    private HttpTags httpTags;

    public ServletInfoTypeHandler(ContentCachingRequestWrapper request, HttpServletResponse response){
        this.request = request;
        this.response = response;
        this.httpTags = new HttpTags();
    }

    /**
     * 记录url、method、statusCode
     * @return
     */
    @Override
    public HttpTags simple() {
        httpTags.setUrl(request.getRequestURI());
        httpTags.setMethod(request.getMethod());
        httpTags.setStatusCode(response.getStatus());
        return httpTags;
    }

    /**
     * 相对于simple 加上parameterMap、requestBody
     * @return
     */
    @Override
    public HttpTags normal() {
        simple();
        Map<String,String> parameterMap = new HashMap<>();
        request.getParameterMap().forEach((key,values)->{
            if(values == null || values.length == 0){
                parameterMap.put(key,null);
            }else if(values.length>1){
                StringBuilder s = new StringBuilder();
                for(String value:values){
                    s.append(",").append(value);
                }
                parameterMap.put(key,s.substring(1));
            }else{
                parameterMap.put(key,values[0]);
            }
        });
        httpTags.setParameterMap(parameterMap);
        String contentType = request.getContentType();
        if(!StringUtils.hasLength(contentType)){
            return httpTags;
        }
        try {
            MediaType mediaType = MediaType.parseMediaType(contentType);
            if(MediaType.APPLICATION_JSON_VALUE.equals(mediaType.getType())){
                httpTags.setRequestBody(new String(request.getContentAsByteArray(),request.getCharacterEncoding()));
            }
        }catch (Exception e){

        }
        return httpTags;
    }

    /**
     * 相对于normal 加上headers
     * @return
     */
    @Override
    public HttpTags full() {
        normal();
        Enumeration<String> enumeration = request.getHeaderNames();
        if(enumeration!=null){
            Map<String,String> map = new HashMap<>();
            while(enumeration.hasMoreElements()){
                String name = enumeration.nextElement();
                map.put(name,request.getHeader(name));
            }
            httpTags.setHeaders(map);
        }
        return httpTags;
    }
}
