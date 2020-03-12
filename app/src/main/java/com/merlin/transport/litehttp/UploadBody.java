package com.merlin.transport.litehttp;

import com.alibaba.fastjson.JSON;
import com.merlin.api.Label;
import com.merlin.api.What;
import com.merlin.debug.Debug;

import org.json.JSONException;
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
        uploadFile(mFile,What.WHAT_REPLACE,sink);
        JSONObject json=new JSONObject();
        try {
            Debug.D(getClass(),"Sending upload terminal signal."+mFile);
            json.put(Label.LABEL_LENGTH,-1);
            writeHead(json.toString(),sink);
        } catch (Exception e) {
            Debug.E(getClass(),"Exception send file upload terminal signal.e="+e,e);
            e.printStackTrace();
        }
    }

    private boolean writeHead(String text,BufferedSink sink) throws Exception{
        byte[] headBytes=null!=text?text.getBytes("utf-8"):null;
        int headLength=null!=headBytes?headBytes.length:-1;
        if (headLength<=0||null==sink){
            throw new Exception("Make file head fail "+headBytes);
        }
        sink.write(intToByteArray(headLength));
        sink.write(headBytes);
        sink.flush();
        Debug.D(getClass(),"Head length."+headLength);
        return true;
    }

    private boolean uploadFile(File file,int coverMode,BufferedSink sink){
        if (null!=file&&null!=sink){
            final String name=file.getName();
            final boolean isDirectory=file.isDirectory();
            final long length=isDirectory?What.WHAT_NOT_FILE:file.length();
            try {
                String folder=".\\linqiang";
                JSONObject json=new JSONObject();
                json.put(Label.LABEL_FOLDER,folder);
                json.put(Label.LABEL_NAME,name);
                json.put(Label.LABEL_LENGTH,length);
                json.put(Label.LABEL_MODE,coverMode);
                writeHead(json.toString(),sink);
                if (isDirectory){
//                    for ()
                }else{
                    byte[] buffer=new byte[1024*1024*2];
                    FileInputStream fis=new FileInputStream(file);
                    int read;
                    while (0<=(read=fis.read(buffer))){
                        sink.write(buffer,0, read);
                    }
                    Debug.D(getClass(),"Finish send one file "+file+" to "+folder+" "+name);
                }
            } catch (Exception e) {
                Debug.E(getClass(),"Can't upload one file while exception."+e, e);
                e.printStackTrace();
            }
        }
        return false;
    }

    private byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }
}
