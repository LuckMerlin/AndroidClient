package com.merlin.transport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

abstract class UploadBody extends RequestBody {

    private final String mPath;

    protected abstract void onTransportProgress(long uploaded,long total,float speed);

    public UploadBody(String path){
        mPath=path;
    }

    @Override
    public long contentLength(){
        String path=mPath;
        return null!=path&&path.length()>0?new File(path).length():-1;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("application/otcet-stream");
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        String path=mPath;
        final File file=null!=path&&path.length()>0?new File(path):null;
        boolean succeed=false;
        if (null!=file&&file.exists()){
            long fileLength = file.length();
            int bufferSize=1024;
            byte[] buffer = new byte[bufferSize];
            FileInputStream in = new FileInputStream(file);
            long uploaded = 0;
            try {
                int read;
                succeed=true;
                while ((read = in.read(buffer)) != -1) {
                    uploaded += read;
                    sink.write(buffer, 0, read);
                    onTransportProgress(uploaded,fileLength,-1);
                }
            }catch (Exception e){
                succeed=false;
            }finally {
                in.close();
            }
        }
    }

}
