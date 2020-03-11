package com.merlin.transport.litehttp;

import com.alibaba.fastjson.JSON;
import com.merlin.api.Label;
import com.merlin.api.What;
import com.merlin.debug.Debug;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public final class UploadBody extends RequestBody  {
    private final File mFile;

    public UploadBody(File file){
        mFile=file;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("application/otcet-stream");
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
         uploadFile(mFile,sink);
         sink.flush();
    }

    private boolean uploadFile(File file,BufferedSink sink){
        if (null!=file&&null!=sink){
            final String name=file.getName();
            final boolean isDirectory=file.isDirectory();
            final long length=isDirectory?What.WHAT_NOT_FILE:file.length();
            try {
                JSONObject json=new JSONObject();
                json.put(Label.LABEL_FOLDER,"/linqiang");
                json.put(Label.LABEL_NAME,name);
                json.put(Label.LABEL_LENGTH,length);
                String head=json.toString();
                byte[] headBytes=null!=head?head.getBytes("utf-8"):null;
                int headLength=null!=headBytes?headBytes.length:-1;
                if (headLength<=0){
                    throw new Exception("Make file head fail "+headBytes);
                }
                sink.writeInt(Integer.MAX_VALUE);
//                sink.write(headBytes);
                if (!isDirectory){
//                    FileInputStream fis=new FileInputStream(file);

                }
            } catch (Exception e) {
                Debug.E(getClass(),"Can't upload one file while exception."+e, e);
                e.printStackTrace();
            }
        }
        return false;
    }
}
