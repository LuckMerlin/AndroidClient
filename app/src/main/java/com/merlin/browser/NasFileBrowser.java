package com.merlin.browser;

import android.content.Context;
import android.view.View;

import com.merlin.api.Address;
import com.merlin.api.Canceler;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.FileMeta;
import com.merlin.bean.NasFolder;
import com.merlin.model.FileBrowserModel;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static com.merlin.api.Label.LABEL_PATH;

public class NasFileBrowser extends FileBrowser implements Label {

    private interface Api {
        @POST(Address.PREFIX_FILE+"/directory/browser")
        @FormUrlEncoded
        Observable<Reply<NasFolder>> queryFiles(@Field(LABEL_PATH) String path, @Field(LABEL_FROM) int from,
                                                @Field(LABEL_TO) int to);
    }


    public NasFileBrowser(Context context, ClientMeta meta,Callback callback){
        super(context,meta,callback);
    }

    @Override
    protected Canceler onPageLoad(Object path, int from, OnApiFinish finish) {
        return null!=path&&path instanceof String?call(prepare(Api.class, Address.URL,null).queryFiles(
                (String)path, from,from+50),null,null,finish):null;
    }

    @Override
    protected boolean onShowFileDetail(View view, FileMeta meta, String debug) {

        return false;
    }

    @Override
    protected boolean onSetAsHome(View view, String path, String debug) {

        return false;
    }
}
