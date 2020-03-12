package com.merlin.model;

import android.content.Context;
import android.view.View;

import com.merlin.adapter.NasBrowserAdapter;
import com.merlin.api.Address;
import com.merlin.api.ApiList;
import com.merlin.api.Callback;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.FileMeta;
import com.merlin.bean.FModify;
import com.merlin.bean.FolderData;
import com.merlin.bean.NasFile;
import com.merlin.bean.NasFolder;
import com.merlin.client.R;
import com.merlin.client.databinding.FileDetailBinding;
import com.merlin.debug.Debug;
import com.merlin.dialog.Dialog;
import com.merlin.server.Retrofit;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static com.merlin.api.What.WHAT_SUCCEED;

public class NasBrowserModel extends BrowserModel<NasFile> implements Label {
    private final Retrofit mRetrofit=new Retrofit();
    private interface Api {
        @POST(Address.PREFIX_FILE_BROWSER)
        @FormUrlEncoded
        Observable<Reply<NasFolder>> queryFiles(@Field(LABEL_PATH) String path, @Field(LABEL_FROM) int from,
                                                @Field(LABEL_TO) int to);

        @POST(Address.PREFIX_USER_REBOOT)
        Observable<Reply> rebootClient();

        @POST(Address.PREFIX_FILE+"/delete")
        @FormUrlEncoded
        Observable<Reply<ApiList<String>>> deleteFile(@Field(LABEL_PATH) List<String> paths);

        @POST(Address.PREFIX_FILE+"/scan")
        @FormUrlEncoded
        Observable<Reply<NasFile>> scan(@Field(LABEL_PATH) String path,@Field(LABEL_ENABLE) boolean recursive);

        @POST(Address.PREFIX_FILE+"/detail")
        @FormUrlEncoded
        Observable<Reply<NasFile>> getDetail(@Field(LABEL_PATH) String path);

        @POST(Address.PREFIX_FILE+"/create")
        @FormUrlEncoded
        Observable<Reply<FModify>> createFile(@Field(LABEL_PATH) String path, @Field(LABEL_NAME) String name, @Field(LABEL_FOLDER) boolean folder);

        @POST(Address.PREFIX_FILE+"/rename")
        @FormUrlEncoded
        Observable<Reply<FModify>> renameFile(@Field(LABEL_PATH) String path, @Field(LABEL_NAME) String name);

    }

    public NasBrowserModel(Context context,ClientMeta meta,ClientCallback callback){
        super(context,meta,callback);
        setAdapter(new NasBrowserAdapter(){
            @Override
            protected final Retrofit.Canceler onPageLoad(String path, int from, OnApiFinish<Reply<FolderData<NasFile>>> finish) {
                return null!=path?call(prepare(Api.class).queryFiles(path, from,from+50),(OnApiFinish<Reply<FolderData<NasFile>>>)(what, note, data, arg)->{
                    if (null!=finish){
                        finish.onApiFinish(what,note,data,arg);
                    }
                    if (what== What.WHAT_SUCCEED){
                        if (null!=callback){
                            callback.onPageDataLoad(NasBrowserModel.this,null!=data?data.getData():null);
                        }
                    }
                }):null;
            }
        });

    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        if (!super.onTapClick(view,clickCount,resId,data)){
        }
        return true;
    }

    @Override
    protected boolean onSingleTapClick(View view, int resId, Object data) {
            switch (resId){
//                case R.string.open:
//                    return null!=data&&data instanceof File_ &&open((File_)data,"After open tap click.");
                case R.string.reboot:
                    return rebootClient("After reboot tap click.");
                case R.string.scan:
                    return null!=data&&data instanceof NasFile&&scan((NasFile)data,false)||true;
//                case R.string.detail:
//                    return null!=data&&data instanceof NasFile &&showFileDetail((NasFile)data);
//                case R.string.delete:
//                    List<NasFile> list=null!=data&&data instanceof NasFile ?new ArrayList<>():null;
//                    return null!=list&&list.add((NasFile)data)&&deleteFile(list,"After delete tap click.");
//                case R.string.rename:
//                    return null!=data&&data instanceof NasFile &&renameFile((NasFile)data);
                case R.string.download:
                    return downloadFile(null!=data&&data instanceof NasFile ?(NasFile)data:null,"After cancel tap click.");
                case R.string.copy:
                    return copyFile(null!=data&&data instanceof NasFile ?(NasFile)data:null,"After copy tap click.");
                case R.string.move:
                    return moveFile(null!=data&&data instanceof NasFile ?(NasFile)data:null,"After move tap click.");
                case R.string.upload:
                    return null!=data&&data instanceof NasFile &&uploadFile((NasFile)data);
                default:
                    return super.onSingleTapClick(view,resId,data);

            }
    }

    @Override
    public boolean onLongClick(View view, int clickCount, int resId, Object data) {
        if (!super.onLongClick(view,clickCount,resId,data)){
            switch (resId){
                case R.string.scan:
                    return null!=data&&data instanceof NasFile&&scan((NasFile)data,true)||true;
            }
        }
        return true;
    }

    private boolean moveFile(NasFile meta, String debug){
        if (null!=meta&&isMode(MODE_MOVE)||entryMode(MODE_MOVE,debug)){
            setProcessing(meta,"While start move file "+(null!=debug?debug:"."));
            return true;
        }
        return false;
    }

    private boolean copyFile(NasFile meta, String debug){
        if (null!=meta&&isMode(MODE_COPY)||entryMode(MODE_COPY,debug)){
            setProcessing(meta,"While start copyx file "+(null!=debug?debug:"."));
            return true;
        }
        return false;
    }

    private boolean uploadFile(NasFile meta){
        String path=null!=meta?meta.getPath():null;
        if (null==path||path.length()<=0 ||!meta.isDirectory()){
            toast(R.string.pathInvalid);
            return false;
        }
        return false;
    }

    private boolean downloadFile(NasFile meta, String debug){
        String path=null!=meta?meta.getPath():null;
        if (null!=path&&path.length()>0){
            ArrayList<NasFile> list=new ArrayList<>();
            list.add(meta);
            return downloadFile(list,debug);
        }
        toast(R.string.pathInvalid);
        return false;
    }

    private boolean downloadFile(ArrayList<NasFile> files, String debug){
        if (null==files||files.size()<=0){
            return false;
        }
//        return TransportService.download(getContext(),files,debug);
        return false;
    }

    @Override
    protected boolean onCreateFile(boolean dir, int mode, String folder, String name, OnApiFinish<Reply<FModify>> finish, String debug) {
        return null!=call(prepare(Api.class).createFile(folder,name,dir),finish);
    }

    @Override
    protected boolean onRenameFile(String path, String name, int mode, OnApiFinish<Reply<FModify>> finish, String debug) {
        return null!=call(prepare(Api.class).renameFile(path,name),finish);
    }

    @Override
    protected boolean onDeleteFile(List<String> files, OnApiFinish<Reply<ApiList<String>>> finish, String debug) {
        return null!=call(prepare(Api.class).deleteFile(files),finish);
    }

    @Override
    protected boolean onOpenFile(FileMeta meta, String debug) {
        toast(R.string.pathInvalid);
        return false;
    }

    //    private boolean deleteFile(List<NasFile> files, String debug){
//        final int length=null!=files?files.size():-1;
//        if (length>0){
//            Dialog dialog=new Dialog(getViewContext());
//            NasFile first=files.get(0);
//            String name=null!=first?first.getName():null;
//            String message=""+(length==1?(null!=name?(""+getText(first.isDirectory()? R.string.folder:R.string.file)+" "+name):""):getText(R.string.items,length));
//            return dialog.create().title(R.string.delete).message(getText(R.string.deleteSure,message)).left(R.string.sure).right(R.string.cancel).show((view, clickCount,  resId, data)->{
//                dialog.dismiss();
//                if (resId ==R.string.sure){
//                    List<String> paths=new ArrayList<>();
//                    Map<String, NasFile> map=new HashMap<>(length);
//                    for (NasFile meta:files) {
//                        String path=null!=meta?meta.getPath():null;
//                        if (null!=path&&path.length()>0){
//                            paths.add(path);
//                            map.put(path,meta);
//                        }
//                    }
//                    if (null!=paths&&paths.size()>0){
//                        return null!=call(Api.class,(OnApiFinish<Reply<ApiList<String>>>)(what, note, data3, arg)->{
//                            toast(note);
//                            if (what==WHAT_SUCCEED){
//                                List<String> deletedPaths=null!=data3?data3.getData():null;
//                                BrowserAdapter adapter=getBrowserAdapter();
//                                int size=null!=deletedPaths&&null!=adapter?deletedPaths.size():-1;
//                                if (size>0){
//                                    List<NasFile> deleted=new ArrayList<>(size);
//                                    for (String  path:deletedPaths) {
//                                        NasFile child=null!=path?map.get(path):null;
//                                        if (null!=child){
//                                            deleted.add(child);
//                                        }
//                                    }
//                                    adapter.remove(deleted,debug);
//                                }
//                            }
//                        }).deleteFile(paths);
//                    }
//                }
//                return true;
//            },false);
//        }
//        Debug.D(getClass(),"Can't delete file.");
//        return false;
//    }


    @Override
    protected boolean onShowFileDetail(FileMeta file, String debug) {
        NasFile meta=null!=file&&file instanceof FileMeta?((NasFile)file):null;
        String path=null!=meta?meta.getPath():null;
        FileDetailBinding binding=null==path||path.length()<=0?null:inflate(R.layout.file_detail);
        if (null==binding){
            toast(R.string.pathInvalid);
            return false;
        }
        binding.setFile(meta);
        binding.setLoadState(What.WHAT_INVALID);
        final Dialog dialog=new Dialog(getViewContext());
        dialog.setContentView(null!=binding?binding.getRoot():null).show(( view, clickCount, resId, data)->{
            return true;
        },false);
        return null!=call(prepare(Api.class).getDetail(path),(OnApiFinish<Reply<NasFile>>)(what, note, data2, arg)->{
            NasFile detail=what==WHAT_SUCCEED&&null!=data2?data2.getData():null;
            if (null!=detail){
                binding.setFile(detail);
            }
            binding.setLoadState(what);
        });
    }

    private boolean rebootClient(String debug){
        Dialog dialog=new Dialog(getViewContext());
        dialog.create().title(R.string.reboot).left(R.string.sure).right(R.string.cancel).show((view,clickCount,resId,data)-> {
            if (resId==R.string.sure){
                Debug.D(getClass(),"Reboot client meta "+(null!=debug?debug:"."));
                call(prepare(Api.class).rebootClient(),(OnApiFinish<Reply>)(what, note, data2, arg)-> toast(note));
            }
            dialog.dismiss();
            return true;
        });
        return true;
    }

    private boolean scan(NasFile file, boolean recursive){
        String path=null!=file?file.getPath():null;
        return null!=path&&path.length()>0&&null!=call(prepare(Api.class).scan(path,recursive),(OnApiFinish<Reply>)(what, note, data2, arg)->toast(note));
    }

    protected final String getClientUrl(){
        ClientMeta meta=getClientMeta();
        return null!=meta?meta.getUrl():null;
    }

    protected final<T> Retrofit.Canceler call(Observable<T> observable, Callback...callbacks){
        Retrofit retrofit=null!=observable?mRetrofit:null;
        return null!=retrofit?retrofit.call(observable,callbacks):null;
    }

    protected final <T> T prepare(Class<T> cls){
        String url=getClientUrl();
        if (null==url||url.length()<=0){
            Debug.W(getClass(),"Can't load nas folder with NULL url."+url);
            url="";
        }
        Retrofit retrofit=mRetrofit;
        return retrofit.prepare(cls,url);
    }

}
