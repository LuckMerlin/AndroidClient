package com.merlin.transport;

import com.merlin.api.Address;
import com.merlin.api.ApiList;
import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.bean.ClientMeta;
import com.merlin.debug.Debug;
import com.merlin.retrofit.Retrofit;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;


public abstract class Uploader extends Transporter{
    Retrofit retrofit=new Retrofit();

    private interface Api{
        @Multipart
        @POST(Address.PREFIX_FILE+"/upload")
        Observable<Reply<ApiList<String>>> upload(@PartMap Map<String, RequestBody> map, @Part List<MultipartBody.Part> parts);
    }

    public boolean upload(Collection collection, boolean interactive, int coverMode, ClientMeta meta, String folder, String debug) {
        if (null != collection && collection.size() > 0) {
            List<String> paths=new ArrayList<>();
            for (Object obj : collection) {
                if (null!=obj&&obj instanceof String){
                    paths.add((String)obj);
                }
            }
            return upload(paths,interactive,coverMode,meta,folder,debug);
        }
        return false;
    }

    public boolean upload(List<String> paths, boolean interactive, int coverMode, ClientMeta meta, String folder, String debug) {
        if (null==paths||paths.size()<=0){
            return false;
        }
        final String url=null!=meta?meta.getUrl():null;
        if (null==url||url.length()<=0){
            return false;
        }
        List<String> exist=null;
        Map<String, String> partMap = new HashMap<>();
//        RequestBody requestFile =RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
//        RequestBody description =RequestBody.create(MediaType.parse("multipart/form-data"), "安卓端上传的文件");
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder=null!=folder?builder.addFormDataPart(Label.LABEL_FOLDER, folder):builder;
        builder.addFormDataPart(Label.LABEL_MODE, Integer.toString(coverMode));
        for (String path:paths) {
            File file=null!=path?new java.io.File(path):null;
            if (null==file||!file.exists()){
                Debug.W(getClass(),"Give up upload one file which not exist."+path);
                continue;
            }
            if (!file.canRead()){
                Debug.W(getClass(),"Give up upload one file which NONE read permission."+path);
                continue;
            }
            Debug.D(getClass(),"Upload file "+path+" to "+url);
            RequestBody body = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), body);
//            builder.addFormDataPart("file[]", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        }
        return false;
//        RequestBody requestBody = builder.build();
//        return null!=retrofit.call(url,Api.class,null,null,null,null).upload(requestBody,folder);
    }
}

