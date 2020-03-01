package com.merlin.transport;

import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.merlin.api.Address;
import com.merlin.api.ApiList;
import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.bean.ClientMeta;
import com.merlin.debug.Debug;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public abstract class Uploader extends Transporter{
//    Retrofit retrofit=new Retrofit();

    private interface Api{
        @Multipart
        @POST(Address.PREFIX_FILE+"/upload")
        Observable<Reply> upload(@Part List<MultipartBody.Part> parts);
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
        final List<MultipartBody.Part> parts=new ArrayList<>();
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
            MultipartBody.Part part = MultipartBody.Part.createFormData("files", file.getName(), body);
            parts.add(part);
//            MultipartBody.Part body =
//                    MultipartBody.Part.createFormData("file", file.getName(), requestFile);

//            builder.addFormDataPart("file[]", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        }
        call(prepare(Api.class,url).upload(parts));
        return false;
    }
}

