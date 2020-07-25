package com.merlin.file.transport;

import com.merlin.api.Label;
import com.merlin.bean.Path;
import com.merlin.task.file.HttpDownloadTask;

import java.io.IOException;
import java.net.HttpURLConnection;

public class NasFileDownloadTask extends HttpDownloadTask {
    private final String mPath;

    public NasFileDownloadTask(Path from, String to) {
        super(from.getName(),from.getHostUri(), to,POST);
        mPath=null!=from?from.getPath():null;
    }

    @Override
    protected HttpURLConnection createHttpConnect(String urlPath, String method) throws IOException {
        HttpURLConnection connection= super.createHttpConnect(urlPath, method);
        if (null!=connection){
            inflateHeader(connection, Label.LABEL_PATH, mPath);
        }
        return connection;
    }

}
