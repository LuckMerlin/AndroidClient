package com.merlin.browser;

import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.bean.FolderData;
import com.merlin.bean.Path;
import com.merlin.debug.Debug;
import com.merlin.dialog.Dialog;
import com.merlin.file.R;
import com.merlin.lib.Canceler;
import com.merlin.server.Client;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static com.merlin.api.Label.LABEL_FROM;
import static com.merlin.api.Label.LABEL_PATH;
import static com.merlin.api.Label.LABEL_TO;

public class NasFileBrowser extends FileBrowser {

    private interface Api{
        @POST("/file/browser")
        @FormUrlEncoded
        Observable<Reply<FolderData<Path>>> queryFiles(@Field(LABEL_PATH) String path, @Field(LABEL_FROM) int from,
                                                       @Field(LABEL_TO) int to);
        @POST("/user/reboot")
        Observable<Reply> rebootClient();
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
    protected boolean onShowPathDetail(Path meta, String debug) {
        return false;
    }

    @Override
    protected boolean onSetAsHome(String path, OnApiFinish<Reply<String>> finish, String debug) {

        return false;
    }

    @Override
    protected boolean onCreatePath(boolean dir, int coverMode, String folder, String name, OnApiFinish<Reply<Path>> finish, String debug) {
        return false;
    }

    @Override
    protected boolean onRenamePath(String path, String name, int coverMode, OnApiFinish<Reply<Path>> finish, String debug) {
        return false;
    }

    @Override
    protected Canceler onPageLoad(Object path, int from, OnApiFinish finish) {
        return call(prepare(Api.class, getClientHost(),null).queryFiles((String)path, from,from+50),null,null,finish);
    }
}
