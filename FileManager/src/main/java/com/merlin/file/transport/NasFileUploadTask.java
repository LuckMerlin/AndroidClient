package com.merlin.file.transport;

import com.merlin.api.Label;
import com.merlin.bean.Path;
import com.merlin.task.file.HttpDownloadTask;
import com.merlin.task.file.HttpUploadTask;
import com.task.debug.Debug;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

public class NasFileUploadTask extends HttpUploadTask {
    private final String mPath;

    public NasFileUploadTask(String from, Path toFolder) {
        this(null!=toFolder&&toFolder.isDirectory()?toFolder.getPath():null,from,
                null!=toFolder&&toFolder.isDirectory()?toFolder.getHostUri():null);
    }

    private NasFileUploadTask(String targetFolder,String from,String to){
        super(from,null!=to?to+"/file/upload":null);
        mPath=targetFolder;
    }

    @Override
    protected void onUploadPrepared(HttpURLConnection connection) {
        super.onUploadPrepared(connection);
        if (null!=connection){
            String path=mPath;
            if (null!=path&&path.length()>0){
                try {
                    Debug.D("AAAAAAAAAAA "+path);
                    connection.setRequestProperty(Label.LABEL_PATH, URLEncoder.encode(path,"utf-8"));
                } catch (UnsupportedEncodingException e) {
                    Debug.E("Exception set nas upload path to header."+e,e);
                    e.printStackTrace();
                }
            }
        }
    }
}
