package com.merlin.transport;

import android.util.Log;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.FileUpload;
import com.merlin.debug.Debug;
import com.merlin.server.Retrofit;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public final class Upload extends AbsTransport<Canceler> {

    private interface Api{
        @Multipart
        @POST(Address.PREFIX_FILE+"/upload")
        Observable<Reply> upload(@Part List<MultipartBody.Part> parts);

        @POST(Address.PREFIX_FILE+"/upload/prepare")
        @FormUrlEncoded
        Observable<Reply> uploadPrepare(@Field(Label.LABEL_PATH) List<String> uploads);
    }

    public Upload(String fromPath,String toFolder,String name,ClientMeta meta,Integer coverMode){
        super(fromPath,toFolder,name,meta,coverMode);
    }

    @Override
    protected Canceler onStart(OnTransportUpdate update,Retrofit retrofit) {
        if (null==retrofit){
            Debug.W(getClass(),"Can't upload file which retrofit is NULL.");
            notifyFinish(false,TRANSPORT_ERROR,"File is NULL .",null,null,null,update);
            return null;
        }
        final ClientMeta client=getClient();
        final String url=null!=client?client.getUrl():null;
        if (null==url||url.length()<=0){
            Debug.W(getClass(),"Can't add upload file which client url invalid."+url);
            notifyFinish(false,TRANSPORT_ERROR,"Client url is invalid.",null,null,null,update);
            return null;
        }
        final String pathSep=null!=client?client.getPathSep():null;
        if (null==pathSep||pathSep.length()<=0){
            Debug.W(getClass(),"Can't add upload file which client path sep invalid."+pathSep);
            notifyFinish(false,TRANSPORT_ERROR,"Client path sep is invalid.",null,null,null,update);
            return null;
        }
        final String fromPath=getFromPath();
        if (null==fromPath||fromPath.length()<=0){
            Debug.W(getClass(),"Can't add upload file which path invalid."+fromPath);
            notifyFinish(false,TRANSPORT_ERROR,"Path is invalid.",null,null,null,update);
            return null;
        }
        final String toFolder=getToFolder();
        final String folder=null!=toFolder&&toFolder.length()>0?toFolder:client.getFolder();
        if (null==folder||folder.length()<=0){
            Debug.W(getClass(),"Can't add upload file which folder invalid."+folder);
            notifyFinish(false,TRANSPORT_ERROR,"Folder is invalid.",null,null,null,update);
            return null;
        }
        final String name=getName();
        final Canceler canceler=new Canceler();
        final String charset="UTF-8";
        final File file=new java.io.File(fromPath);
        Debug.D(getClass(),"Uploading file "+fromPath+" to "+url+" "+folder);
        final List<String> files=new ArrayList<>();
        iteratorAllFiles(pathSep,file,file.getParent(),folder,files);//Iterate to add all  files
        Debug.D(getClass(),"AAAAAAAA "+files.size());
        if (null!=files&&files.size()>0){
            return retrofit.call(retrofit.prepare(Api.class, url).uploadPrepare(files), (OnApiFinish)( what, note, data, arg)-> {

            })?canceler:null;
        }
        notifyFinish(false,TRANSPORT_EMPTY,"None file need upload.",null,null,null,update);
        return null;
    }

    private void iteratorAllFiles(String pathSep,File file,final String root,final String folder,final List<String> files){
       if (null!=pathSep&&null!=file&&null!=files&&null!=root&&null!=folder){
           final String path=file.getAbsolutePath();
           if (null!=path&&path.length()>0){
               boolean isDirectory=file.isDirectory();
               if (isDirectory){
                   File[] children=file.listFiles();
                   Debug.D(getClass(),"AAAeeeeeeAAAAAA "+children);
                   if (null!=children&&children.length>0){
                       for (File child:children) {
                           Debug.D(getClass(),"dddddd "+child);
                           iteratorAllFiles(pathSep,child,root,folder,files);
                       }
                   }
               }
               Debug.D(getClass(),"AAAA "+path+" "+folder);
               String targetPath=path.replaceFirst(root,folder);
               Debug.D(getClass(),"AAAA "+targetPath);
               if (null!=targetPath&&targetPath.length()>0){
                   Debug.D(getClass()," "+path+(isDirectory?pathSep:"")+" "+targetPath);
//                   JSONObject json=new JSONObject();
//                   json.put(Label.LABEL_FROM,path+(isDirectory?pathSep:""));
//                   json.put(Label.LABEL_TO,targetPath);
                   files.add(targetPath+(isDirectory?pathSep:""));
//                   files.put(path+(isDirectory?pathSep:""),targetPath);
//                   files.add(new FileUpload(path,targetPath,isDirectory?Label.LABEL_FOLDER:null));
               }
           }
       }
    }

    private boolean upload(File file,String fromRoot,String toFolder){
        if (null==file||null==fromRoot){
            Debug.W(getClass(),"Give up upload one file which NONE file invalid."+file+" "+fromRoot);
            return false;
        }
        if (!file.canRead()){
            Debug.W(getClass(),"Give up upload one file which NONE read permission."+file);
//            notifyFinish(false,TRANSPORT_ERROR,"File NONE permission.",null,null,null,update);
            return false;
        }
//        parts.add(MultipartBody.Part.createFormData(URLEncoder.encode(file.getAbsolutePath(), charset), URLEncoder.encode(file.getName(), charset), new UploadBody(file){
//          @Override
//          protected void onTransportProgress(long uploaded, long total, float speed) {
//
//          }}));
        if (file.isDirectory()){
            File[] files=file.listFiles();

        }
//        Debug.D(getClass(),"Upload file "+path+" to "+url+" "+folder+" ");
//        final Canceler canceler=new Canceler();
//        final List<MultipartBody.Part> parts=new ArrayList<>();
//        try {
//            final String charset="UTF-8";
//            if (null!=folder) {
//                parts.add(MultipartBody.Part.createFormData(Label.LABEL_PARENT, URLEncoder.encode(folder,charset)));
//            }
////                if (null!=name) {
////                    parts.add(MultipartBody.Part.createFormData(Label.LABEL_NAME, URLEncoder.encode(name,charset)));
////                }
//            if (file.isFile()) {
//                parts.add(MultipartBody.Part.createFormData(URLEncoder.encode(file.getAbsolutePath(), charset), URLEncoder.encode(file.getName(), charset), new UploadBody(file){
//                    @Override
//                    protected void onTransportProgress(long uploaded, long total, float speed) {
//
//                    }}));
//            }
//            final Retrofit.OnApiFinish<Reply> mCallback=new Retrofit.OnApiFinish<Reply>() {
//                @Override
//                public void onApiFinish(boolean succeed, int what, String note, Reply data, Object arg) {
//                    Debug.D(getClass(),"上传 结束 "+data);
//                    if (null!=data&&data.isSuccess()){
////                            retrofit.call(retrofit.prepare(Api.class, url).upload(parts), Schedulers.io(),this);
//                    }
//                } };
//            return retrofit.call(retrofit.prepare(Api.class, url).upload(parts), Schedulers.io(),mCallback)?canceler:null;
//        } catch (UnsupportedEncodingException e) {
//            Debug.E(getClass(),"Exception when upload file.e="+e+" "+file, e);
//            e.printStackTrace();
//            notifyFinish(false,TRANSPORT_FAIL,"File upload exception.",null,null,null,update);
//        }
        return false;
    }

    final void notifyFinish(boolean succeed, Integer what, String note, Long uploaded, Long total, Float speed, OnTransportUpdate update){
        if (null!=update&&null!=what){
            update.onTransportUpdate(true,what,note,uploaded,total,speed);
        }
    }

//    private boolean uploadFile(Retrofit retrofit,String url,File file,String folder,String name, OnTransportUpdate update){
//        if (null!=file){
//            final String charset="UTF-8";
//            final String targetName=null!=name&&name.length()>0?name:file.getName();
//            if (null==file||!file.exists()||targetName==null||targetName.length()<=0){
//                Debug.W(getClass(),"Give up upload one file which not exist."+file);
//                notifyFinish(false,TRANSPORT_ERROR,"File not exist.",null,null,null,update);
//                return false;
//            }
//            final List<MultipartBody.Part> parts=new ArrayList<>();
//            try {
//                if (null!=folder) {
//                    parts.add(MultipartBody.Part.createFormData(Label.LABEL_PARENT, URLEncoder.encode(folder,charset)));
//                }
//                if (null!=name) {
//                    parts.add(MultipartBody.Part.createFormData(Label.LABEL_NAME, URLEncoder.encode(name,charset)));
//                }
//                if (file.isFile()) {
//                    parts.add(MultipartBody.Part.createFormData(URLEncoder.encode(file.getAbsolutePath(), charset), URLEncoder.encode(targetName, charset), new UploadBody(file){
//                        @Override
//                        protected void onTransportProgress(long uploaded, long total, float speed) {
//
//                        }
//                    }));
//                }
//                final Retrofit.OnApiFinish<Reply> mCallback=new Retrofit.OnApiFinish<Reply>() {
//                @Override
//                public void onApiFinish(boolean succeed, int what, String note, Reply data, Object arg) {
//                    if (null!=data&&data.isSuccess()){
//                        retrofit.call(retrofit.prepare(Api.class, url).upload(parts), Schedulers.io(),this);
//                    }
//                }
//                };
//            } catch (UnsupportedEncodingException e) {
//                Debug.E(getClass(),"Exception when upload file.e="+e+" "+file, e);
//                e.printStackTrace();
//                notifyFinish(false,TRANSPORT_FAIL,"File upload exception.",null,null,null,update);
//                return false;
//            }
//        }
//        return false;
//    }



}
