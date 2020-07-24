package com.merlin.file.transport;

import com.merlin.api.Label;
import com.merlin.bean.Path;
import com.merlin.task.file.HttpDownloadTask;
import com.task.debug.Debug;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

public class NasFileDownloadTask extends HttpDownloadTask {
    private final String mPath;

    public NasFileDownloadTask(Path from, String to) {
        super(from.getName(),from.getHostUri(), to);
        mPath=null!=from?from.getPath():null;
    }

    @Override
    protected void onDownloadPrepared(HttpURLConnection connection,long currentLength) {
        super.onDownloadPrepared(connection,currentLength);
        if (null!=connection){
            String path=mPath;
            if (null!=path&&path.length()>0){
                try {
                    connection.setRequestProperty(Label.LABEL_PATH, URLEncoder.encode(path,"utf-8"));
                } catch (UnsupportedEncodingException e) {
                    Debug.E("Exception set nas download path to header."+e,e);
                    e.printStackTrace();
                }
            }
            connection.setRequestProperty(Label.LABEL_POSITION,Long.toString(currentLength));
        }
    }
}
