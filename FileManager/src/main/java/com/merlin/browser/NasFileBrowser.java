package com.merlin.browser;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.bean.FolderData;
import com.merlin.bean.Path;
import com.merlin.debug.Debug;
import com.merlin.dialog.Dialog;
import com.merlin.file.R;
import com.merlin.file.databinding.NasFileDetailBinding;
import com.merlin.lib.Canceler;
import com.merlin.server.Client;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import com.merlin.api.Label;

import com.merlin.api.What;

import java.io.IOException;

public class NasFileBrowser extends FileBrowser {

    private interface Api{
        @POST("/file/browser")
        @FormUrlEncoded
        Observable<Reply<FolderData<Path>>> queryFiles(@Field(Label.LABEL_PATH) String path, @Field(Label.LABEL_FROM) int from,
                                                       @Field(Label.LABEL_TO) int to);
        @POST("/user/reboot")
        Observable<Reply> rebootClient();

        @POST("/user/home")
        @FormUrlEncoded
        Observable<Reply<String>> setHome(@Field(Label.LABEL_PATH) String path);

        @POST("/file/create")
        @FormUrlEncoded
        Observable<Reply<Path>> createPath(@Field(Label.LABEL_FOLDER) boolean folder, @Field(Label.LABEL_PARENT) String parent,
                                           @Field(Label.LABEL_NAME) String name, @Field(Label.LABEL_MODE) int coverMode);

        @POST("/file/rename")
        @FormUrlEncoded
        Observable<Reply<Path>> renameFile(@Field(Label.LABEL_PATH) String path, @Field(Label.LABEL_NAME) String name,
                                           @Field(Label.LABEL_MODE) int coverMode);

        @POST("/file/detail")
        @FormUrlEncoded
        Observable<Reply<Path>> getDetail(@Field(Label.LABEL_PATH) String path);
//        Observable<Reply<Path>> getDetail(@Body RequestBody file);

        @POST("/file/delete")
        @FormUrlEncoded
        Observable<Reply<Path>> deletePath(@Field(Label.LABEL_PATH) String path);
    }

    public NasFileBrowser(Client meta, Callback callback) {
        super(meta, callback);
    }

    @Override
    protected boolean onReboot(String debug) {
        Dialog dialog=new Dialog(getViewContext());
        return dialog.create().title(R.string.reboot).left(R.string.sure).right(R.string.cancel).show((view, clickCount, resId, data)-> {
            if (resId==R.string.sure){
                Debug.D(getClass(),"Reboot client meta "+(null!=debug?debug:"."));
                call(prepare(Api.class,null,null).rebootClient(),null,
                        null,(OnApiFinish<Reply>)(what, note, data2, arg)-> toast(note));
            }
            dialog.dismiss();
            return true;
        });
    }

    @Override
    protected boolean onOpenPath(Path meta, String debug) {

        return false;
    }

    @Override
    protected boolean onShowPathDetail(Path pathObj, String debug) {
        String path=null!=pathObj ?pathObj.getPath():null;
        NasFileDetailBinding binding=null==path||path.length()<=0?null:inflate(R.layout.nas_file_detail);
        if (null==binding){
            return toast(R.string.pathInvalid)&&false;
        }
        binding.setFile(pathObj);
        binding.setLoadState(What.WHAT_INVALID);
        final Dialog dialog=new Dialog(getAdapterContext());
        dialog.setContentView(null!=binding?binding.getRoot():null,true).show((v, clickCount, resId, data)->{
            return true;
        },false);
        return null!=call(prepare(Api.class,null,null).getDetail(path),null,
                null,null,(OnApiFinish<Reply<Path>>)(what, note, data2, arg)->{
            Path detail=what==What.WHAT_SUCCEED&&null!=data2?data2.getData():null;
            binding.setFile(detail);
            binding.setLoadState(what);
        });
    }

    @Override
    protected boolean onSetAsHome(String path, OnApiFinish<Reply<String>> finish, String debug) {
        return null!=call(prepare(Api.class,null,null).setHome(path),null,null,finish);
    }

    @Override
    protected boolean onCreatePath(boolean dir, int coverMode, String folder, String name, OnApiFinish<Reply<Path>> finish, String debug) {
        return null!=call(prepare(Api.class,null,null).createPath(dir,folder, name,coverMode),null,null,finish);
    }

    @Override
    protected boolean onRenamePath(String path, String name, int coverMode, OnApiFinish<Reply<Path>> finish, String debug) {
        return null!=call(prepare(Api.class,null,null).renameFile(path,name, coverMode),null,null,finish);
    }

    @Override
    protected Canceler onPageLoad(String path, int from, OnApiFinish<Reply<PageData<Path>>> finish) {
        return call(prepare(Api.class, getClientHost(),null).queryFiles(path, from,from+50),null,null,finish);
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        if(super.onTapClick(view, clickCount, resId, data)){
            return true;
        }
        switch (resId){
            case R.string.download:
                Debug.D(getClass(),"EEEEEEEEE "+data);
                break;
        }
        return false;
    }
}
