package com.merlin.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.activity.PhotoPreviewActivity;
import com.merlin.api.Label;
import com.merlin.bean.LocalPhoto;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemPhotoGridBinding;
import com.merlin.debug.Debug;
import com.merlin.photo.Photo;
import com.merlin.view.OnTapClick;

import java.util.ArrayList;
import java.util.List;

public abstract class PhotoGridAdapter extends PageAdapter<String, Photo> implements OnTapClick {
    private int mSpanCount;
    private int mMaxChoose=0;
    private boolean mVisibleCamera;
    private ArrayList<Photo> mChoose;

    public PhotoGridAdapter(){
        this(3,false,0);
    }

    public PhotoGridAdapter(int spanCount,boolean visibleCamera,int maxChoose){
        mSpanCount=spanCount;
        mVisibleCamera=visibleCamera;
        mMaxChoose=maxChoose;
    }

    public final int maxChoose(Integer maxChoose){
        if (null!=maxChoose){
            int curr=mMaxChoose;
            if (curr!=maxChoose){
                mMaxChoose=maxChoose;
            }
            mChoose=null;
        }
        return mMaxChoose;
    }

    @Override
    protected Integer onResolveViewTypeLayoutId(int viewType) {
        return viewType==TYPE_DATA||viewType==TYPE_EMPTY||viewType==TYPE_TAIL? R.layout.item_photo_grid:null;
    }

    public final boolean visibleCamera(Boolean visible){
        if (null!=visible){
            boolean curr=mVisibleCamera;
            if (curr!=visible){
                mVisibleCamera=visible;
                notifyDataSetChanged();
            }
        }
        return mVisibleCamera;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType, ViewDataBinding binding, int position, Photo data, @NonNull List<Object> payloads) {
        if (null!=binding&&binding instanceof ItemPhotoGridBinding){
            data=mVisibleCamera&&(viewType==TYPE_EMPTY||viewType==TYPE_TAIL)?new CameraPhoto(null):data;
            ((ItemPhotoGridBinding)binding).setPhoto(data);
            ((ItemPhotoGridBinding)binding).setChooseEnable(viewType==TYPE_DATA&&mMaxChoose>0);
            List<Photo> list=mChoose;
            ((ItemPhotoGridBinding)binding).setExistChoose(null!=data&&null!=list&&list.contains(data));
        }
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        int spanCount=mSpanCount;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(rv.getContext(), spanCount<=0?1:spanCount);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rv.addItemDecoration(new GridSpacingItemDecoration(spanCount, 10, true));
        gridLayoutManager.setSmoothScrollbarEnabled(true);
        return gridLayoutManager;
    }

    public static final class CameraPhoto implements Photo{
        private final int mResId=android.R.drawable.ic_menu_camera;

        @Override
        public Object getLoadUrl() {
            return mResId;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        private CameraPhoto(Parcel in) {
        }
        @Override
        public void writeToParcel(Parcel dest, int flags) {

        }

        public static final Parcelable.Creator<CameraPhoto> CREATOR = new Parcelable.Creator<CameraPhoto>(){

            @Override
            public CameraPhoto createFromParcel(Parcel source) {
                return new CameraPhoto(source);
            }

            @Override
            public CameraPhoto[] newArray(int size) {
                return new CameraPhoto[size];
            }

        };
    }

    @Override
    public final boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (clickCount){
            case 1:
                switch (resId){
                    case android.R.drawable.ic_menu_camera:
                        return startCamera("After camera tap click.");
                    default:
                        if (null!=view&&null!=data&&data instanceof Photo){
                            return view instanceof ImageView?showPhoto(view,(Photo) data,"After photo tap click."):
                                    view instanceof CheckBox?choosePhoto(view,(LocalPhoto)data,"After photo tap click."):false;
                        }
                }
                return false;
            case 2:
                Context context=null!=view&&null!=data&&data instanceof Photo?view.getContext():null;
                if (null!=context&&context instanceof Activity){
                    Intent intent=new Intent();
                    ArrayList<Photo> list=mChoose;
                    if (null!=list){
                        intent.putParcelableArrayListExtra(Label.LABEL_DATA,list);
                    }
                    ((Activity)context).setResult(Activity.RESULT_OK,intent);
                    ((Activity)context).finish();
                    cleanChoose(false);
                    return true;
                }
                return false;
        }
        return false;
    }

    private boolean startCamera(String debug){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {  //如果是7.0以上，使用FileProvider，否则会报错
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); //设置图片保存的格式
//        return startActivity(intent,REQUEST_CODE_CAPTURE_RAW);
        return false;
    }

    private boolean choosePhoto(View view,Photo photo,String debug){
        if (null!=photo&&null!=view){
            List<Photo> choose=mChoose;
            if (view instanceof CheckBox&&((CheckBox)view).isChecked()){
                if (null!=choose&&choose.size()>=mMaxChoose){
                    Context context=view.getContext();
                    ((CheckBox)view).setChecked(false);
                    Toast.makeText(context,R.string.alreadyMax,Toast.LENGTH_SHORT).show();
                    return false;
                }
                choose=null==choose?(mChoose=new ArrayList<>()):choose;
                if (!choose.contains(photo)&&choose.add(photo)){
                    return true;
                }
            }else if(null!=choose&&choose.remove(photo)){
                return true;
            }
        }
        return false;
    }

    private boolean showPhoto(View view,Photo photo, String debug){
        ArrayList<LocalPhoto> list=null!=view&&null!=photo&&photo instanceof LocalPhoto?new ArrayList<>(1):null;
        return null!=list&&list.add((LocalPhoto)photo)&& PhotoPreviewActivity.start(view.getContext(),list,0,debug);
    }

    private boolean cleanChoose(boolean notify){
        ArrayList<Photo> list=mChoose;
        if (null!=list){
            int size=list.size();
            list.clear();
            mChoose=null;
            if (notify){
                notifyItemMoved(0,size);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onDetachedRecyclerView(RecyclerView recyclerView) {
        super.onDetachedRecyclerView(recyclerView);
        cleanChoose(false);
    }
}
