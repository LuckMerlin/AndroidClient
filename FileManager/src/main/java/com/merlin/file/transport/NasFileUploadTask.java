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
    private final String mFolder;
    private final String mName;

    public NasFileUploadTask(String from, Path toFolder,String name) {
        this(null!=toFolder&&toFolder.isDirectory()?toFolder.getParent():null,name,from,
                null!=toFolder&&toFolder.isDirectory()?toFolder.getHostUri():null);
    }

    private NasFileUploadTask(String folder,String name,String from,String toUri){
        super(from,null!=toUri?toUri+"/file/upload":null);
        mFolder=folder;
        mName=name;
    }

    @Override
    protected void onUploadPrepared(HttpURLConnection connection) {
        super.onUploadPrepared(connection);
        if (null!=connection){
            String folder=mFolder;
            if (null!=folder&&folder.length()>0){
                try {
                    connection.setRequestProperty(Label.LABEL_PATH, URLEncoder.encode(folder,"utf-8"));
                } catch (UnsupportedEncodingException e) {
                    Debug.E("Exception set nas upload path to header."+e,e);
                    e.printStackTrace();
                }
            }
            String name=mName;
            if (null!=name&&name.length()>0){
                try {
                    connection.setRequestProperty(Label.LABEL_NAME, URLEncoder.encode(name,"utf-8"));
                } catch (UnsupportedEncodingException e) {
                    Debug.E("Exception set nas upload path to header."+e,e);
                    e.printStackTrace();
                }
            }
        }
    }
}
