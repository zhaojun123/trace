package com.izkml.trace.core.util;

import java.io.*;

public class IOUtils {

    public  static String convertStreamToString(InputStream is, String charset) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,charset));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
