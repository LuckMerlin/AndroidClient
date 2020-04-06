package com.merlin.browser;

import android.content.Context;
import android.view.View;

import com.merlin.api.Address;
import com.merlin.api.ApiMap;
import com.merlin.api.Canceler;
import com.merlin.api.CoverMode;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.FileMeta;
import com.merlin.bean.FolderData;
import com.merlin.bean.NasFile;
import com.merlin.bean.Path;
import com.merlin.client.R;
import com.merlin.client.databinding.FileDetailBinding;
import com.merlin.dialog.Dialog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static com.merlin.api.What.WHAT_SUCCEED;

public class NasFileBrowser extends FileBrowser implements Label {

    private interface Api {
        @POST(Address.PREFIX_FILE+"/directory/browser")
        @FormUrlEncoded
        Observable<Reply<FolderData<NasFile>>> queryFiles(@Field(LABEL_PATH) String path, @Field(LABEL_FROM) int from,
                                                          @Field(LABEL_TO) int to);
        @POST(Address.PREFIX_FILE+"/detail")
        @FormUrlEncoded
        Observable<Reply<NasFile>> getDetail(@Field(LABEL_PATH) String path);

        @POST(Address.PREFIX_FILE+"/home")
        @FormUrlEncoded
        Observable<Reply<String>> setHome(@Field(LABEL_PATH) String path);

        @POST(Address.PREFIX_FILE+"/rename")
        @FormUrlEncoded
        Observable<Reply<Path>> renameFile(@Field(LABEL_PATH) String path, @Field(LABEL_NAME) String name,@Field(LABEL_MODE) int coverMode);

        @POST(Address.PREFIX_FILE+"/create")
        @FormUrlEncoded
        Observable<Reply<Path>> createPath(@Field(LABEL_FOLDER) boolean folder,@Field(LABEL_PARENT) String parent,@Field(LABEL_NAME) String name,@Field(LABEL_MODE) int coverMode);
    }

    public NasFileBrowser(ClientMeta meta,Callback callback){
        super(meta,callback);
    }

    @Override
    protected Canceler onPageLoad(Object path, int from, OnApiFinish finish) {
        return null!=path&&path instanceof String?call(prepare(Api.class, Address.URL,null).queryFiles(
                (String)path, from,from+50),null,null,finish):null;
    }

    @Override
    protected boolean onShowPathDetail(FileMeta meta, String debug) {
        String path=null!=meta&&meta instanceof NasFile?meta.getPath(false):null;
        FileDetailBinding binding=null==path||path.length()<=0?null:inflate(R.layout.file_detail);
        if (null==binding){
            return toast(R.string.pathInvalid)&&false;
        }
        binding.setFile((NasFile)meta);
        binding.setLoadState(What.WHAT_INVALID);
        final Dialog dialog=new Dialog(getAdapterContext());
        dialog.setContentView(null!=binding?binding.getRoot():null,true).show((v, clickCount, resId, data)->{
            return true;
        },false);
        return null!=call(prepare(Api.class,Address.URL,null).getDetail(path),null,null,null,(OnApiFinish<Reply<NasFile>>)(what, note, data2, arg)->{
            NasFile detail=what==WHAT_SUCCEED&&null!=data2?data2.getData():null;
            binding.setFile(detail);
            binding.setLoadState(what);
        });
    }

    @Override
    protected boolean onRenamePath(String path, String name, int coverMode, OnApiFinish<Reply<Path>> finish, String debug) {
        return null!=call(prepare(Api.class,Address.URL,null).renameFile(path,name, coverMode),null,null,finish);
    }

    @Override
    protected boolean onCreatePath(boolean dir, int coverMode, String folder, String name, OnApiFinish<Reply<Path>> finish, String debug) {
        return null!=call(prepare(Api.class,Address.URL,null).createPath(dir,folder, name,coverMode),null,null,finish);
    }

    @Override
    protected FileProcess onCreatePathsProcess(int mode, ArrayList<FileMeta> paths, String folder, Integer coverMode, String debug) {
        switch (mode){
            case R.string.delete:
                return new FileDeleteProcess(mode,paths);
        }
        return null;
    }

    @Override
    protected boolean onSetAsHome(String path,OnApiFinish<Reply<String>> finish, String debug) {
        return null!=call(prepare(Api.class,Address.URL,null).setHome(path),null,null,finish);
    }

    @Override
    protected boolean onOpenPath(FileMeta meta, String debug) {

        return false;
    }
}
