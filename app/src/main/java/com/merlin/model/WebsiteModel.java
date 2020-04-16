package com.merlin.model;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.activity.LocalPhotoChooseActivity;
import com.merlin.adapter.WebsiteBannerAdapter;
import com.merlin.api.Canceler;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.bean.WebsiteImage;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemBannerBinding;
import com.merlin.debug.Debug;
import com.merlin.view.OnTapClick;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class WebsiteModel  extends Model implements Label, OnTapClick, Model.OnActivityResult {
    private final String mUrl="http://192.168.0.6" +
            ":5005";
//    private final String mUrl="http://172.16.20.212:5005";
    private final static int PHOTO_CHOOSE_REQUEST_CODE=234234;

    private interface Api{
        @POST("/travel/images")
        @FormUrlEncoded
        Observable<Reply<PageData<WebsiteImage>>> getBanners(@Field(LABEL_NAME) String name, @Field(LABEL_FROM) int from, @Field(LABEL_TO) int to);
    }

    private final ObservableField<RecyclerView.Adapter> mAdapter=new ObservableField<>();
    private final WebsiteBannerAdapter mBannerAdapter=new WebsiteBannerAdapter(){
        @Override
        protected Canceler onPageLoad(String arg, int from, OnApiFinish<Reply<PageData<ItemBannerBinding>>> finish) {
            return call(prepare(Api.class,mUrl,null).getBanners(arg,from,from+10),null,finish);
        }
    };

    public WebsiteModel(){
        mAdapter.set(mBannerAdapter);
        queryBanners();
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        Debug.D(getClass(),"AAAAAAAAAAAAA "+resId);
        switch (resId){
            case R.string.add:
                startActivity(new Intent(view.getContext(), LocalPhotoChooseActivity.class),PHOTO_CHOOSE_REQUEST_CODE);
                break;
        }
        return true;
    }

    public boolean queryBanners(){
        WebsiteBannerAdapter bannerAdapter=mBannerAdapter;
        return null!=bannerAdapter&&bannerAdapter.loadPage("","");
    }

    public ObservableField<RecyclerView.Adapter> getAdapter() {
        return mAdapter;
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case PHOTO_CHOOSE_REQUEST_CODE:
                Bundle bundle=null!=data?data.getExtras():null;
                Debug.D(getClass(),"AAA  "+bundle);
                break;
        }
    }
}
