package com.merlin.transport;

import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.bean.ClientMeta;
import com.merlin.debug.Debug;
import com.merlin.server.Retrofit;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public final class Upload extends Transport<UploadBody> {

    private interface Api{
        @Multipart
        @POST(Address.PREFIX_FILE+"/upload")
        Observable<Reply> upload(@Part List<MultipartBody.Part> parts);
    }

    public Upload(String fromPath,String toFolder,String name,ClientMeta meta,Integer coverMode){
        super(fromPath,toFolder,name,meta,coverMode);
    }

    @Override
    protected UploadBody onStart(OnTransportUpdate update,Retrofit retrofit) {
        if (null==retrofit){
            Debug.W(getClass(),"Can't upload file which retrofit is NULL.");
            notifyFinish(false,TRANSPORT_ERROR,"File is NULL .",null,null,null,update);
            return null;
        }
        final ClientMeta meta=getClient();
        final String url=null!=meta?meta.getUrl():null;
        if (null==url||url.length()<=0){
            Debug.W(getClass(),"Can't add upload file which client url invalid."+url);
            notifyFinish(false,TRANSPORT_ERROR,"Client url is invalid.",null,null,null,update);
            return null;
        }
        final String path=getFromPath();
        if (null==path||path.length()<=0){
            Debug.W(getClass(),"Can't add upload file which path invalid."+path);
            notifyFinish(false,TRANSPORT_ERROR,"Path is invalid.",null,null,null,update);
            return null;
        }
        List<String> exist=null;
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        final String folder=getToFolder();
        builder=null!=folder?builder.addFormDataPart(Label.LABEL_FOLDER, folder):builder;
        builder.addFormDataPart(Label.LABEL_MODE, Integer.toString(getCoverMode()));
        final List<MultipartBody.Part> parts=new ArrayList<>();
        final String charset="UTF-8";
        File file=new java.io.File(path);
        final String name=getName();
        final String targetName=null!=name&&name.length()>0?name:file.getName();if (null==file||!file.exists()||targetName==null||targetName.length()<=0){
            Debug.W(getClass(),"Give up upload one file which not exist."+path);
            notifyFinish(false,TRANSPORT_ERROR,"File not exist.",null,null,null,update);
            return null;
        }
        if (!file.canRead()){
            Debug.W(getClass(),"Give up upload one file which NONE read permission."+path);
            notifyFinish(false,TRANSPORT_ERROR,"File NONE permission.",null,null,null,update);
            return null;
        }
        Debug.D(getClass(),"Upload file "+path+" to "+url);
        final UploadBody uploadBody=new UploadBody(path){
            @Override
            public void onTransportProgress(long uploaded, long total, float speed) {
                if (null!=update){
                    update.onTransportUpdate(false,TRANSPORT_PROGRESS,null,uploaded,total,speed);
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
            notifyFinish(false,TRANSPORT_FAIL,"File upload exception.",null,null,null,update);
            return null;
        }
        return retrofit.call(retrofit.prepare(Api.class, url).upload(parts),(Retrofit.OnApiFinish<Reply>)(succeed, what, note, data, arg)-> {
            succeed=null!=data&&data.isSuccess();
            notifyFinish(false,succeed?TRANSPORT_SUCCEED:TRANSPORT_FAIL,"Upload finish.",null,null,null,update);
        })?uploadBody:null;
    }

    final void notifyFinish(boolean finish, Integer what, String note, Long uploaded, Long total, Float speed, OnTransportUpdate update){
        if (null!=update&&null!=what){
            update.onTransportUpdate(true,what,note,uploaded,total,speed);
        }
    }

}
