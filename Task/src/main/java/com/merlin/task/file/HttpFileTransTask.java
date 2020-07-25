package com.merlin.task.file;

import com.task.debug.Debug;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public abstract class HttpFileTransTask<T,V> extends FileTransTask<T,V>{

    protected final static String POST="POST";
    protected final static String GET="GET";
    protected final static String HEAD="HEAD";

    public HttpFileTransTask(String name,T from, V to, String method) {
        super(name,from, to, method);
    }

    protected final boolean inflateHeader(HttpURLConnection connection,String key,String value){
        if (null!=connection&&null!=key&&key.length()>0&&null!=value){
            try {
                connection.setRequestProperty(key, URLEncoder.encode(value,"utf-8"));
                return true;
            } catch (UnsupportedEncodingException e) {
                Debug.E("Exception set value to header."+e,e);
                e.printStackTrace();
            }
        }
        return false;
    }

    protected HttpURLConnection createHttpConnect(String urlPath,String method) throws IOException {
        URL url=null!=urlPath&&urlPath.length()>0?new URL(urlPath):null;
        HttpURLConnection conn = null!=url?(HttpURLConnection) url.openConnection():null;
        if (null!=conn){
            conn.setRequestMethod(null!=method&&method.length()>0?method:GET);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setUseCaches(false);
            return conn;
        }
        return null;
    }
}
