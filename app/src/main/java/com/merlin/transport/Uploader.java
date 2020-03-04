package com.merlin.transport;

import android.content.Context;

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


public final class Uploader extends Transporter implements Transporter.Callback {
    private final Map<String,UploadBody> mUploading=new ConcurrentHashMap<>();

    private interface Api{
        @Multipart
        @POST(Address.PREFIX_FILE+"/upload")
        Observable<Reply> upload(@Part List<MultipartBody.Part> parts);
    }

    public Uploader(Context context){
        super(context);
    }

    public final boolean upload(Collection collection, String folder, boolean interactive, int coverMode,
                                ClientMeta meta,OnStatusChange progress, String debug) {
        if (null != collection && collection.size() > 0) {
            for (Object obj : collection) {
                if (null!=obj&&obj instanceof String){
                    add(new Upload((String)obj,folder,null,meta,null),interactive,progress,debug);
                }
            }
        }
        return false;
    }

    @Override
    protected boolean onAddTransport(Transport transport,TransportUpdate update) {
        if (null==transport||!(transport instanceof Upload)){
            Debug.W(getClass(),"Can't add upload file which type NOT match."+transport);
            return false;
        }
        final ClientMeta meta=transport.getClient();
        final String url=null!=meta?meta.getUrl():null;
        if (null==url||url.length()<=0){
            Debug.W(getClass(),"Can't add upload file which client url invalid."+url);
            return false;
        }
        final String path=null!=transport?transport.getFromPath():null;
        if (null==path||path.length()<=0){
            Debug.W(getClass(),"Can't add upload file which path invalid."+path);
            return false;
        }
        final Upload upload=(Upload)transport;
        List<String> exist=null;
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        final String folder=null!=upload?upload.getToFolder():null;
        builder=null!=folder?builder.addFormDataPart(Label.LABEL_FOLDER, folder):builder;
        builder.addFormDataPart(Label.LABEL_MODE, Integer.toString(upload.getCoverMode()));
        final List<MultipartBody.Part> parts=new ArrayList<>();
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
                public void onTransportFinish(boolean succeed) {
                        //Do nothing
                }

                @Override
                public void onTransportProgress(long uploaded, long total, float speed) {
                        if (null!=update){
                            update.onTransportProgress(uploaded,total,speed);
                        }
                }
            };
            parts.add(MultipartBody.Part.createFormData(URLEncoder.encode(path,charset), URLEncoder.encode(targetName,charset), uploadBody));
            if (null!=uploading){
                uploading.put(path,uploadBody);
            }
            if (null!=folder) {
                parts.add(MultipartBody.Part.createFormData(Label.LABEL_FOLDER, folder));
            }
        } catch (UnsupportedEncodingException e) {
            Debug.E(getClass(),"Exception when upload file.e="+e+" "+path, e);
            e.printStackTrace();
            return false;
        }
        return call(prepare(Api.class, url).upload(parts),(OnApiFinish<Reply>)(succeed,what,note,data,arg)-> {
            if (interactive){
                toast(note);
            }
            notifyStatusChange(TRANSPORT_REMOVE,upload,progress);
        });
    }


    private static abstract class UploadBody extends RequestBody implements TransportUpdate{
        private final String mPath;

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
                    onTransportFinish(succeed);
                }
            }
        }
    }
}

