package com.merlin.file.transport;

import com.merlin.bean.Path;
import com.merlin.task.file.HttpDownloadTask;
import java.net.HttpURLConnection;

public class NasFileDownloadTask extends HttpDownloadTask {
    private final String mPath;

    public NasFileDownloadTask(Path from, String to) {
        super(from.getName(),from.getHostUri(), to,POST);
        mPath=null!=from?from.getPath():null;
    }

    @Override
    protected void onDownloadPrepared(HttpURLConnection connection,long currentLength) {
        super.onDownloadPrepared(connection,currentLength);
    }
}
