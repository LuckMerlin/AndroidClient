package com.merlin.transport;

import com.merlin.api.Label;
import com.merlin.debug.Debug;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.crypto.spec.DESedeKeySpec;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

abstract class UploadBody extends RequestBody{
    private final File mFile;
    private boolean mCancel=false;

    protected abstract void onTransportProgress(long uploaded,long total,float speed);

    public UploadBody(File file){
        mFile=file;
    }

    @Override
    public long contentLength(){
        File file=mFile;
        return null!=file&&file.exists()&&file.isFile()?file.length():0;
    }

    @Override
    public MediaType contentType() {
//        return MediaType.parse("multipart/form-data");
        return MediaType.parse("application/otcet-stream");
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        final File file=mFile;
        boolean succeed=false;
        if (null!=file&&file.exists()){
            if (file.isFile()){
                FileInputStream in=null;
                try {
                    long fileLength = file.length();
                    JSONObject json=new JSONObject();
                    json.put(Label.LABEL_NAME,file.getName());
                    json.put(Label.LABEL_LENGTH,fileLength);
                    int bufferSize=1024;
                    byte[] buffer = new byte[bufferSize];
                    in = new FileInputStream(file);
                    long uploaded = 0;
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
                    if (null!=in) {
                        in.close();
                    }
                }
            }
        }
    }

//    private boolean writeFile(BufferedSink sink,File file) throws IOException{
//        if (null!=file) {
//            if (null != sink && file.exists()&&file.isFile()) {
//                FileInputStream in = null;
//                try {
//                    final String charset = "utf-8";
//                    long fileLength = file.length();
//                    JSONObject jsonobject = new JSONObject();
//                    final int folderLength = -1;
//                    jsonobject.put(Label.LABEL_NAME, file.getName());
//                    jsonobject.put(Label.LABEL_LENGTH, file.isDirectory() ? folderLength : fileLength);
//                    String json = jsonobject.toString();
//                    final byte[] bytes = null != json ? json.getBytes(charset) : null;
//                    final int headLength = null != bytes ? bytes.length : -1;
//                    if (headLength <= 0) {
//                        Debug.W(getClass(), "ddddd ");
//                        return false;
//                    }
//                    byte[] result = new byte[4];
//                    result[0] = (byte) ((headLength >> 24) & 0xFF);
//                    result[1] = (byte) ((headLength >> 16) & 0xFF);
//                    result[2] = (byte) ((headLength >> 8) & 0xFF);
//                    result[3] = (byte) (headLength & 0xFF);
//                    for (int i = 0; i < result.length; i++) {
//                        Debug.D(getClass(),""+i+" "+result[i]);
//                    }
//                    Debug.D(getClass(), "长度 " + headLength+" "+json);
//                    sink.write(result);
//                    sink.flush();
                    //
//                    int bufferSize=1024;
//                    byte[] buffer = new byte[bufferSize];
//                    in = new FileInputStream(file);
//                    long uploaded = 0;
//                    if (!mCancel){
//                        int read;
//                        succeed=true;
//                        while ((read = in.read(buffer)) != -1) {
//                            if (mCancel){
//                                break;
//                            }
//                            uploaded += read;
//                            sink.write(buffer, 0, read);
//                            onTransportProgress(uploaded,fileLength,-1);
//                        }
//                    }
//                } catch (Exception e) {
//                    Debug.E(getClass(),"ddd "+e);
//                } finally {
//                    if (null != in) {
//                        in.close();
//                    }
//                }
//            }
//        }
//        return false;
//    }

//
    private void writeFile(BufferedSink sink,File file) throws IOException{
        if (null!=file){
            boolean succeed=false;
            if (null!=sink&&file.exists()){
                FileInputStream in=null;
                try {
                    long fileLength = file.length();
                    JSONObject json=new JSONObject();
                    json.put(Label.LABEL_NAME,file.getName());
                    json.put(Label.LABEL_LENGTH,fileLength);
                    int bufferSize=1024;
                    byte[] buffer = new byte[bufferSize];
                    in = new FileInputStream(file);
                    long uploaded = 0;
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
                    if (null!=in) {
                        in.close();
                    }
                }
            }
        }
    }
}
