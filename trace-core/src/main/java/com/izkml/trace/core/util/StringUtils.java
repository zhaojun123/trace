package com.izkml.trace.core.util;

import java.net.URI;
import java.net.URISyntaxException;

public class StringUtils {

    /**
     * 从一个url中获取 host+port+path的拼装
     * @param url
     * @return
     */
    public static String getHostPortPath(String url){
        try {
            URI uri = new URI(url);
            StringBuilder s = new StringBuilder();
            s.append(uri.getHost());
            if(uri.getPort()>0){
                s.append(":").append(uri.getPort());
            }
            s.append(uri.getPath());
            return s.toString();
        } catch (URISyntaxException e) {
            return url;
        }

    }

}
