package com.merlin.transport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

abstract class UploadBody extends RequestBody implements Canceler{
    private final File mFile;
    private boolean mCancel;

    protected abstract void onTransportProgress(long uploaded,long total,float speed);

    public UploadBody(File file){
        mFile=file;
    }

    @Override
    public final boolean cancel(boolean cancel) {
        boolean curr=mCancel;
        mCancel=cancel;
        return cancel!=curr;
    }

    @Override
    public long contentLength(){
        File file=mFile;
        return null!=file&&file.isFile()?file.length():0;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("application/otcet-stream");
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        final File file=mFile;
        boolean succeed=false;
        if (null!=file&&file.exists()){
            if (file.isDirectory()){
                //Not need upload directory
            }else{
                long fileLength = file.length();
                int bufferSize=1024;
                byte[] buffer = new byte[bufferSize];
                FileInputStream in = new FileInputStream(file);
                long uploaded = 0;
                try {
                    if (!mCancel){
                        int read;
                        succeed=true;
                        while ((read = in.read(buffer)) != -1) {
                            if (mCancel){
                                break;
                            }
                            uploaded += read;
                            sink.write(buffer, 0, read);
                            onTransportProgress(uploaded,fileLength,-1);
                        }
                    }
                }catch (Exception e){
                    succeed=false;
                }finally {
                    in.close();
                }
            }
        }
    }

}
