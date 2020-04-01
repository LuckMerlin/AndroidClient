package com.merlin.browser;

import com.merlin.api.What;
import com.merlin.debug.Debug;
import com.merlin.util.Closer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class FileUploadBody extends RequestBody {
    private final String mFilePath;

    protected void onTransportProgress(long uploaded,long total,float speed){
        //Do nothing
    }

    protected boolean isCancel(){
        return false;
    }

    public FileUploadBody(String filePath){
        mFilePath=filePath;
    }

    @Override
    public final long contentLength() {
        String filePath=mFilePath;
        File file = null!=filePath&&filePath.length()>0?new File(filePath):null;
        return null != file && file.exists() && file.isFile() ? file.length() : 0;
    }

    @Override
    public final MediaType contentType() {
        return MediaType.parse("application/otcet-stream");
    }

    @Override
    public final void writeTo(BufferedSink sink) {
        String filePath=mFilePath;
        final File file = null!=filePath&&filePath.length()>0?new File(filePath):null;
        if (null != file && file.exists()) {
            if (file.isFile()) {
                FileInputStream in = null;
                long fileLength = file.length();
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                try {
                    in = new FileInputStream(file);
                    long uploaded = 0;
                    if (!isCancel()) {
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            if (isCancel()) {
                                Debug.D(getClass(),"Cancel file upload convey.");
                                throw new IOException(Integer.toString(What.WHAT_CANCEL));
                            }
                            sink.write(buffer, 0, read);
                            uploaded += read;
                            onTransportProgress(uploaded, fileLength, -1);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    new Closer().close(in);
                }
            }
        }
    }
}