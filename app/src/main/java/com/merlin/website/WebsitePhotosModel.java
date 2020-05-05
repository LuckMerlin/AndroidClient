package com.merlin.website;

import android.view.View;

import com.merlin.adapter.PhotoAdapter;
import com.merlin.api.Canceler;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.bean.Path;
import com.merlin.model.Model;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class WebsitePhotosModel extends Model implements Label {
    private PhotoAdapter mAdapter=new PhotoAdapter(3,false){
        @Override
        protected Canceler onPageLoad(String arg, int from, OnApiFinish<Reply<PageData<Path>>> finish) {
            return call(prepare(Api.class,WebsiteModel.mUrl).getCategories(arg,"image/",from,from+10),finish);
        }
    };

    private interface Api{
        @POST("/file/list")
        @FormUrlEncoded
        Observable<Reply<PageData<Path>>> getCategories(@Field(LABEL_NAME) String name, @Field(LABEL_FORMAT) String format, @Field(LABEL_FROM) int from, @Field(LABEL_TO) int to);
    }

    @Override
    protected void onRootAttached(View root) {
        super.onRootAttached(root);
        mAdapter.loadPage("","After root attached.");
    }

    public PhotoAdapter getAdapter() {
        return mAdapter;
    }
}
