package com.merlin.transport;
import com.merlin.api.Address;
import com.merlin.api.ApiList;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.NasFile;
import com.merlin.debug.Debug;
import com.merlin.server.Retrofit;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import com.merlin.api.Canceler;

public final class Upload extends AbsTransport<Canceler> {

    private interface Api{
        @Multipart
        @POST(Address.PREFIX_FILE+"/upload")
        Observable<Reply<String>> upload(@Part List<MultipartBody.Part> parts);

        @POST(Address.PREFIX_FILE+"/upload/prepare")
        @FormUrlEncoded
        Observable<Reply<ApiList<NasFile>>> uploadPrepare(@Field(Label.LABEL_PATH) List<String> uploads);

    }

    public Upload(String fromPath,String toFolder,String name,ClientMeta meta,Integer coverMode){
        super(fromPath,toFolder,name,meta,coverMode);
    }

    @Override
    protected Canceler onStart(OnTransportUpdate update, Retrofit retrofit) {
        if (null==retrofit){
            Debug.W(getClass(),"Can't upload file which retrofit is NULL.");
            notifyFinish(false,TRANSPORT_ERROR,"File is NULL .",update,null);
            return null;
        }
        final ClientMeta client=getClient();
        final String url=null!=client?client.getUrl():null;
        if (null==url||url.length()<=0){
            Debug.W(getClass(),"Can't add upload file which client url invalid."+url);
            notifyFinish(false,TRANSPORT_ERROR,"Client url is invalid.",update,null);
            return null;
        }
        final String pathSep=null!=client?client.getPathSep():null;
        if (null==pathSep||pathSep.length()<=0){
            Debug.W(getClass(),"Can't add upload file which client path sep invalid."+pathSep);
            notifyFinish(false,TRANSPORT_ERROR,"Client path sep is invalid.",update,null);
            return null;
        }
        final String fromPath=getFromPath();
        if (null==fromPath||fromPath.length()<=0){
            Debug.W(getClass(),"Can't add upload file which path invalid."+fromPath);
            notifyFinish(false,TRANSPORT_ERROR,"Path is invalid.",update,null);
            return null;
        }
        final String toFolder=getToFolder();
        final String folder=null!=toFolder&&toFolder.length()>0?toFolder:client.getFolder();
        if (null==folder||folder.length()<=0){
            Debug.W(getClass(),"Can't add upload file which folder invalid."+folder);
            notifyFinish(false,TRANSPORT_ERROR,"Folder is invalid.",update,null);
            return null;
        }
        final String name=getName();
        final String charset="UTF-8";
        final File file=new java.io.File(fromPath);
        Debug.D(getClass(),"Uploading file "+fromPath+" to "+url+" "+folder);
        try {
            final List<MultipartBody.Part> files=new ArrayList<>();
            String targetPath=fromPath.replaceFirst(file.getParent(),folder);
            targetPath=null!=targetPath&&targetPath.length()>0?targetPath.replace(File.separator,pathSep):null;
            if (null!=targetPath&&targetPath.length()>0){
                final Progress progress=new Progress();
                files.add(MultipartBody.Part.createFormData(URLEncoder.encode(targetPath, charset), URLEncoder.encode(file.getName(), charset), new UploadBody(file) {
                    @Override
                    protected void onTransportProgress(long uploaded, long total, float speed) {
                        progress.setSpeed(speed);
                        progress.setTotalSize(total);
                        progress.setDoneSize(uploaded);
                        if (null!=update){
                            update.onTransportUpdate(false, TRANSPORT_PROGRESS,null,progress);
                        }
                    }
                }));
            }
            if (null!=files&&files.size()>0) {
                files.add(MultipartBody.Part.createFormData(Label.LABEL_MODE, Integer.toString(getCoverMode())));
                return retrofit.call(retrofit.prepare(Api.class, url).upload(files),  (OnApiFinish<Reply<String>>)(what, note, data, arg) ->{
                        boolean succeed=what==What.WHAT_SUCCEED;
                        notifyFinish(succeed,succeed?TRANSPORT_SUCCEED:TRANSPORT_FAIL,note,update,null);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//            return retrofit.call(retrofit.prepare(Api.class, url).uploadPrepare(files), (Retrofit.OnApiFinish<Reply<ApiList<NasFile>>>)(succeed, what, note, data, arg)-> {
//                if (succeed&&what== What.WHAT_SUCCEED){
//                    ApiList<NasFile> list=data.getData();
//                    if (null!=list&&list.size()>0){
//                        Block block=new Block(list);
//                        notifyFinish(true,TRANSPORT_PREPARE_BLOCK,"Some file already exist.",update,block);
//                    }else{
//
//                    }
//                }
//            })?canceler:null;
//        }
        notifyFinish(false,TRANSPORT_EMPTY,"None file need upload.",update,null);
        return null;
    }

    private void iteratorAllFiles(String pathSep,File file,final String root,final String folder,final List<MultipartBody.Part> files) throws Exception{
       if (null!=pathSep&&null!=file&&null!=files&&null!=root&&null!=folder){
           final String path=file.getAbsolutePath();
           if (null!=path&&path.length()>0){
               boolean isDirectory=file.isDirectory();
               if (isDirectory){
                   File[] children=file.listFiles();
                   if (null!=children&&children.length>0){
                       for (File child:children) {
                           iteratorAllFiles(pathSep,child,root,folder,files);
                       }
                       return;
                   }
               }
               String targetPath=path.replaceFirst(root,folder);
               targetPath=null!=targetPath&&targetPath.length()>0?targetPath.replace(File.separator,pathSep):null;
               if (null!=targetPath&&targetPath.length()>0){
                   String charset="utf-8";
                   files.add(MultipartBody.Part.createFormData(URLEncoder.encode(isDirectory?targetPath+pathSep:targetPath, charset), URLEncoder.encode(file.getName(), charset), new UploadBody(file){
                   @Override
                   protected void onTransportProgress(long uploaded, long total, float speed) {

                   }}));
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

    final void notifyFinish(boolean succeed, Integer what, String note, OnTransportUpdate update,Object data){
        if (null!=update&&null!=what){
            update.onTransportUpdate(true,what,note,data);
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
