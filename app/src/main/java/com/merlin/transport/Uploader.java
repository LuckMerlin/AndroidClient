package com.merlin.transport;

import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.LocalFile;
import com.merlin.debug.Debug;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public abstract class Uploader extends Transporter{
    private final Map<String,UploadBody> mUploading=new ConcurrentHashMap<>();

    private interface Api{
        @Multipart
        @POST(Address.PREFIX_FILE+"/upload")
        Observable<Reply> upload(@Part List<MultipartBody.Part> parts);
    }

    public final boolean upload(Collection collection, String folder, boolean interactive, int coverMode,
                                ClientMeta meta,OnStatusChange progress, String debug) {
        if (null != collection && collection.size() > 0) {
            for (Object obj : collection) {
                if (null!=obj&&obj instanceof String){
                    upload(new Upload((String)obj,folder,null,meta),interactive,coverMode,meta,progress,debug);
                }
            }
        }
        return false;
    }

    public final synchronized boolean upload(Upload upload, boolean interactive, int coverMode, ClientMeta meta, OnStatusChange progress, String debug) {
        final String path=null!=upload?upload.getPath():null;
        if (null==path||path.length()<=0){
            return false;
        }
        final String url=null!=meta?meta.getUrl():null;
        if (null==url||url.length()<=0){
            return false;
        }
        final Map<String,UploadBody> uploading=mUploading;
        if (null!=uploading&&uploading.containsKey(path)){
            Debug.W(getClass(),"Skip upload one file which already uploading."+path);
            return false;
        }
        List<String> exist=null;
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        final String folder=null!=upload?upload.getFolder():null;
        builder=null!=folder?builder.addFormDataPart(Label.LABEL_FOLDER, folder):builder;
        builder.addFormDataPart(Label.LABEL_MODE, Integer.toString(coverMode));
        final List<MultipartBody.Part> parts=new ArrayList<>();
        Debug.D(getClass(),"Upload file "+path);
        final String charset="UTF-8";
        File file=new java.io.File(path);
        final String name=null!=upload?upload.getName():null;
        final String targetName=null!=name&&name.length()>0?name:file.getName();if (null==file||!file.exists()||targetName==null||targetName.length()<=0){
            Debug.W(getClass(),"Give up upload one file which not exist."+path);
            return false;
        }
        if (!file.canRead()){
            Debug.W(getClass(),"Give up upload one file which NONE read permission."+path);
            return false;
        }
        Debug.D(getClass(),"Upload file "+path+" to "+url);
        try {
            final UploadBody uploadBody=new UploadBody(path){
                @Override
                protected void onUploadProgress(long uploaded, long total) {
                    notifyStatusChange();
                }
            };
            parts.add(MultipartBody.Part.createFormData(URLEncoder.encode(path,charset), URLEncoder.encode(targetName,charset), uploadBody));
            if (null!=uploading){
                uploading.put(path,uploadBody);
            }
        } catch (UnsupportedEncodingException e) {
            Debug.E(getClass(),"Exception when upload file.e="+e+" "+path, e);
            e.printStackTrace();
        }
        if (null!=folder) {
            parts.add(MultipartBody.Part.createFormData(Label.LABEL_FOLDER, folder));
        }

        return call(prepare(Api.class, url).upload(parts),(OnApiFinish<Reply>)(succeed,what,note,data,arg)-> {
            if (interactive){
                toast(note);
            }
        });
    }

    public final Collection<Upload> getUploading(String name){
        Map<String,UploadBody> uploading=mUploading;
        return null;
    }

    public final boolean isUploading(String ...paths){
        Map<String,UploadBody> uploading=null!=paths&&paths.length>0?mUploading:null;
        if (null!=uploading){
            synchronized (uploading){
                for (String path:paths) {
                    if (null!=path&&path.length()>0&&uploading.containsKey(path)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static abstract class UploadBody extends RequestBody{
        private final String mPath;

        protected abstract void onUploadProgress(long uploaded,long total);

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
            if (null!=file&&file.exists()){
                long fileLength = file.length();
                int bufferSize=1024*1024*2;
                byte[] buffer = new byte[bufferSize];
                FileInputStream in = new FileInputStream(file);
                long uploaded = 0;
                try {
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        uploaded += read;
                        sink.write(buffer, 0, read);
                        onUploadProgress(uploaded,fileLength);
                    }
                } finally {
                    in.close();
                }
            }
        }
    }
}

