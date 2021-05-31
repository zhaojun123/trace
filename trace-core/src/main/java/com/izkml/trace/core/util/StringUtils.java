package com.izkml.trace.core.util;

import java.net.URI;
import java.net.URISyntaxException;

public class StringUtils {

    /**
     * 从一个url中获取 Scheme+host+port+path的拼装
     * @param url
     * @return
     */
    public static String getSchemeHostPortPath(String url){
        try {
            URI uri = new URI(url);
            return getSchemeHostPortPath(uri);
        } catch (URISyntaxException e) {
            return url;
        }
    }

    public static String getSchemeHostPortPath(URI uri){
        StringBuilder s = new StringBuilder();
        s.append(uri.getScheme()).append("://").append(uri.getHost());
        if(uri.getPort()>0){
            s.append(":").append(uri.getPort());
        }
        s.append(uri.getPath());
        return s.toString();
    }
}
