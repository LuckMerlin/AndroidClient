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
import com.merlin.api.CoverMode;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.bean.Path;
import com.merlin.bean.WebsiteImage;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemBannerBinding;
import com.merlin.client.databinding.LayoutFileConveyingBinding;
import com.merlin.conveyor.ConveyGroup;
import com.merlin.conveyor.UploadConvey;
import com.merlin.debug.Debug;
import com.merlin.dialog.FileConveyDialog;
import com.merlin.view.OnTapClick;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class WebsiteModel  extends Model implements Label, OnTapClick, Model.OnActivityResult {
    public static final String mUrl="http://192.168.0.6:5005";
//    private final String mUrl="http://172.16.20.212:45678";
    private final static int PHOTO_CHOOSE_REQUEST_CODE=234234;

    private interface Api{
        @POST("/travel/images")
        @FormUrlEncoded
        Observable<Reply<PageData<WebsiteImage>>> getBanners(@Field(LABEL_NAME) String name, @Field(LABEL_FROM) int from, @Field(LABEL_TO) int to);
    }

    private ObservableField<RecyclerView.Adapter> mAdapter=new ObservableField<>();
    private final WebsiteBannerAdapter mBannerAdapter=new WebsiteBannerAdapter(){
        @Override
        protected Canceler onPageLoad(String arg, int from, OnApiFinish<Reply<PageData<WebsiteImage>>> finish) {
            return call(prepare(Api.class,mUrl).getBanners(arg,from,from+10),finish);
        }
    };

    public WebsiteModel(){
        mAdapter.set(mBannerAdapter);
        queryBanners();
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
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

    private boolean uploadFiles(List<Path> paths){
        if (null==paths||paths.size()<=0){
            return toast(R.string.listEmpty)&&false;
        }
        UploadConvey convey=null;
        String folder="lovePhotos";
        ConveyGroup<UploadConvey> group=new ConveyGroup<>();
        boolean empty=true;
        Path remoteFolder=new Path(mUrl,folder,null,null);
        for (Path child:paths) {
            String path=null!=child?child.getPath():null;
            if (null!=(convey=null!=path&&path.length()>0? new UploadConvey(Path.build(path,null),
                    remoteFolder, CoverMode.SKIP):null)&&group.add(convey)){
                empty=false;
            }
        }
        if (empty){
            return toast(R.string.listEmpty)&&false;
        }
        final LayoutFileConveyingBinding binding=inflate(R.layout.layout_file_conveying);
        final FileConveyDialog dialog=new FileConveyDialog(binding);
        dialog.convey(this,group,"");
        return dialog.title(R.string.upload).show();
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case PHOTO_CHOOSE_REQUEST_CODE:
                Bundle bundle=null!=data?data.getExtras():null;
                Object object=null!=bundle?bundle.get(Label.LABEL_DATA):null;
                if (null!=object&&object instanceof ArrayList) {
                    List<Path> paths=new ArrayList<>();
                    for (Object child : (ArrayList) object) {
                        if (null != child&&child instanceof Path) {
                            paths.add((Path)child);
                        }
                    }
                    uploadFiles(paths);
                }
                break;
        }
    }
}
