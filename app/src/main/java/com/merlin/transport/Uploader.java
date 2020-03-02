package com.merlin.transport;

import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.LocalFile;
import com.merlin.debug.Debug;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public abstract class Uploader extends Transporter{

    private interface Api{
        @Multipart
        @POST(Address.PREFIX_FILE+"/upload")
        Observable<Reply> upload(@Part List<MultipartBody.Part> parts);
    }

    public boolean upload(Collection collection, boolean interactive, int coverMode, ClientMeta meta, String folder, String debug) {
        if (null != collection && collection.size() > 0) {
            List<LocalFile> paths=new ArrayList<>();
            for (Object obj : collection) {
                if (null!=obj&&obj instanceof LocalFile){
                    paths.add((LocalFile)obj);
                }
            }
            return upload(paths,interactive,coverMode,meta,folder,debug);
        }
        return false;
    }

    public boolean upload(List<LocalFile> paths, boolean interactive, int coverMode, ClientMeta meta, String folder, String debug) {
        if (null==paths||paths.size()<=0){
            return false;
        }
        final String url=null!=meta?meta.getUrl():null;
        if (null==url||url.length()<=0){
            return false;
        }
        List<String> exist=null;
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder=null!=folder?builder.addFormDataPart(Label.LABEL_FOLDER, folder):builder;
        builder.addFormDataPart(Label.LABEL_MODE, Integer.toString(coverMode));
        final List<MultipartBody.Part> parts=new ArrayList<>();
        Debug.D(getClass(),"Upload file "+paths.size());
        for (LocalFile local:paths) {
            String path=null!=local?local.getPath():null;
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
            parts.add(MultipartBody.Part.createFormData(file.getAbsolutePath(), file.getName(), body));
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
}

