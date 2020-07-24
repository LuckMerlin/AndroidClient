package com.merlin.task.file;
import com.task.debug.Debug;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;


public abstract class HttpUploadTask extends HttpFileTransTask<String, String> {
    private final String mToFolder;
    private final String mToName;

    public HttpUploadTask(String name,String from, String to,String toFolder,String toName) {
        this(name,from,to,null,toFolder,toName);
    }

    public HttpUploadTask(String name,String from, String to,String method,String toFolder,String toName) {
        super(name,from, to,method);
        mToFolder=toFolder;
        mToName=toName;
    }

    public final String getToFolder() {
        return mToFolder;
    }

    public final String getToName() {
        return mToName;
    }

    protected final boolean inflate(HttpURLConnection connection,String key,String value){
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

}
