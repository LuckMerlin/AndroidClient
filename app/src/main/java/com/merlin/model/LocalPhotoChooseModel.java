package com.merlin.model;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;

import androidx.core.content.FileProvider;

import com.merlin.activity.PhotoPreviewActivity;
import com.merlin.adapter.PhotoGridAdapter;
import com.merlin.api.Canceler;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.LocalPhoto;
import com.merlin.debug.Debug;
import com.merlin.photo.LocalPhotoLoader;
import com.merlin.photo.Photo;
import com.merlin.view.OnTapClick;

import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;

public class LocalPhotoChooseModel extends Model implements OnTapClick,Label, Model.OnActivityResult {
    private final static int REQUEST_CODE_CAPTURE_RAW=2008;
    private final LocalPhotoLoader mLoader=new LocalPhotoLoader();
    private final PhotoGridAdapter mAdapter=new PhotoGridAdapter(){
        @Override
        protected Canceler onPageLoad(String arg, int from, OnApiFinish<Reply<PageData<Photo>>> finish) {
            Canceler canceler=(boolean cancel, String debug)-> {
                    return false;
            };
            LocalPhotoLoader loader=mLoader;
            final int pageLimit=10;
            final ArrayList<Photo> photos=new ArrayList<>(pageLimit);
            return null!=finish&&null!=loader&&loader.load(getContentResolver(), from, from + pageLimit, (what, photo,length)-> {
                    switch (what){
                        case LocalPhotoLoader.OnLocalPhotoLoad.WHAT_LOAD_ONE:
                            return null!=photo&&photos.add(photo);
                        case LocalPhotoLoader.OnLocalPhotoLoad.WHAT_FINISH:
                            finish.onApiFinish(What.WHAT_SUCCEED,"Load succeed.",new Reply<PageData<Photo>>
                                    (true,What.WHAT_SUCCEED,"Load succeed.", new PageData(from,photos,length)),null);
                            return false;
                        case LocalPhotoLoader.OnLocalPhotoLoad.WHAT_LOAD_OUT_OF_BOUNDS:
                            finish.onApiFinish(What.WHAT_OUT_OF_BOUNDS,"Out of bounds.",new Reply<PageData<Photo>>
                                    (true,What.WHAT_OUT_OF_BOUNDS,"Out of bounds.", new PageData(from,photos,length)),null);
                            return false;
                    }
                    return false;
            })?canceler:null;
        }
    };

    @Override
    protected void onRootAttached(View root) {
        super.onRootAttached(root);
        mAdapter.loadPage(null,"While model root attached.");
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (clickCount){
            case 1:
                switch (resId){
                    case android.R.drawable.ic_menu_camera:
                        return startCamera("After camera tap click.");
                    default:
                        if (null!=data&&data instanceof LocalPhoto){
                            return showPhoto((LocalPhoto) data,"After photo tap click.");
                        }
                }
                return true;
        }
        return false;
    }

    private boolean showPhoto(Photo photo, String debug){
        ArrayList<LocalPhoto> list=null!=photo&&photo instanceof LocalPhoto?new ArrayList<>(1):null;
        return null!=list&&list.add((LocalPhoto)photo)&&PhotoPreviewActivity.start(getContext(),list,0,debug);
    }

    private boolean startCamera(String debug){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {  //如果是7.0以上，使用FileProvider，否则会报错
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); //设置图片保存的格式
        return startActivity(intent,REQUEST_CODE_CAPTURE_RAW);
    }

    public PhotoGridAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
       Uri uri=requestCode==REQUEST_CODE_CAPTURE_RAW&&null!=data?data.getData():null;
    }
}
