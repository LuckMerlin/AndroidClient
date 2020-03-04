package com.merlin.transport;

import android.content.Context;

import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.LocalFile;
import com.merlin.debug.Debug;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public final class Uploader extends Transporter<Upload,UploadBody> implements Callback {

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
    protected UploadBody onAddTransport(Upload upload,TransportUpdate update,boolean interactive) {
        if (null==upload){
            Debug.W(getClass(),"Can't add upload file which is NULL.");
            return null;
        }
        final ClientMeta meta=upload.getClient();
        final String url=null!=meta?meta.getUrl():null;
        if (null==url||url.length()<=0){
            Debug.W(getClass(),"Can't add upload file which client url invalid."+url);
            return null;
        }
        final String path=null!=upload?upload.getFromPath():null;
        if (null==path||path.length()<=0){
            Debug.W(getClass(),"Can't add upload file which path invalid."+path);
            return null;
        }
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
            return null;
        }
        if (!file.canRead()){
            Debug.W(getClass(),"Give up upload one file which NONE read permission."+path);
            return null;
        }
        Debug.D(getClass(),"Upload file "+path+" to "+url);
        final UploadBody uploadBody=new UploadBody(path){
            @Override
            public void onTransportProgress(long uploaded, long total, float speed) {
                if (null!=update){
                    update.onTransportProgress(uploaded,total,speed);
                }
            }
        };
        try {
            parts.add(MultipartBody.Part.createFormData(URLEncoder.encode(path,charset), URLEncoder.encode(targetName,charset), uploadBody));
            if (null!=folder) {
                parts.add(MultipartBody.Part.createFormData(Label.LABEL_FOLDER, folder));
            }
        } catch (UnsupportedEncodingException e) {
            Debug.E(getClass(),"Exception when upload file.e="+e+" "+path, e);
            e.printStackTrace();
            return null;
        }
        return call(prepare(Api.class, url).upload(parts),(OnApiFinish<Reply>)(succeed,what,note,data,arg)-> {
            if (interactive){
                toast(note);
            }
            if (null!=update){
                update.onTransportFinish(succeed);
            }
        })?uploadBody:null;
    }

}

