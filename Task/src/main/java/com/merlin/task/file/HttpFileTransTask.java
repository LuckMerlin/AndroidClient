package com.merlin.task.file;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class HttpFileTransTask<T,V> extends FileTransTask<T,V>{

    protected final static String POST="POST";
    protected final static String GET="GET";

    public HttpFileTransTask(T from, V to, String method) {
        super(from, to, method);
    }

    protected HttpURLConnection createHttpConnect(String urlPath,String method) throws IOException {
        URL url=null!=urlPath&&urlPath.length()>0?new URL(urlPath):null;
        HttpURLConnection conn = null!=url?(HttpURLConnection) url.openConnection():null;
        if (null!=conn){
            conn.setRequestMethod(null!=method&&method.equalsIgnoreCase("post")?POST:GET);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setUseCaches(false);
            return conn;
        }
        return null;
    }
}
