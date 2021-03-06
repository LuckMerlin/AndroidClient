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
import com.merlin.api.Canceler;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.bean.IPath;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemPhotoGridBinding;
import com.merlin.view.OnTapClick;

import java.util.ArrayList;
import java.util.List;

public class PhotoGridAdapter extends PageAdapter<String, IPath> implements OnTapClick {
    private int mSpanCount;
    private int mMaxChoose=0;
    private boolean mVisibleCamera;
    private ArrayList<IPath> mChoose;
    private int mMax;

    public PhotoGridAdapter(){
        this(Integer.MAX_VALUE);
    }

    public PhotoGridAdapter(int max){
        this(max,3,false,0);
    }

    public PhotoGridAdapter(int spanCount,boolean visibleCamera,int maxChoose){
        this(Integer.MAX_VALUE,spanCount,visibleCamera,maxChoose);
    }

    public PhotoGridAdapter(int max,int spanCount,boolean visibleCamera,int maxChoose){
        mMax=max;
        mSpanCount=spanCount;
        mVisibleCamera=visibleCamera;
        mMaxChoose=maxChoose;
    }

    @Override
    protected Canceler onPageLoad(String arg, int from, OnApiFinish<Reply<PageData<IPath>>> finish) {
        return null;
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

    public final boolean setMax(int max) {
        if (max!=this.mMax){
            this.mMax=max;
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    @Override
    protected Integer onResolveViewTypeLayoutId(int viewType) {
        return viewType==TYPE_DATA||viewType==TYPE_EMPTY||viewType==TYPE_TAIL? R.layout.item_photo_grid:null;
    }

    /**
     * @deprecated
     */
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
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType, ViewDataBinding binding, int position, IPath data, @NonNull List<Object> payloads) {
        if (null!=binding&&binding instanceof ItemPhotoGridBinding){
            Object url=data;
            if (mVisibleCamera&&(viewType==TYPE_EMPTY||viewType==TYPE_TAIL)){
                data=new CameraPhoto(null);
                url=((CameraPhoto)data).mResId;
            }
            ((ItemPhotoGridBinding)binding).setPhoto(data);
            ((ItemPhotoGridBinding)binding).setPhotoUrl(url);
            ((ItemPhotoGridBinding)binding).setChooseEnable(viewType==TYPE_DATA&&mMaxChoose>0);
            List<IPath> list=mChoose;
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

    public static final class CameraPhoto extends IPath {
        private final int mResId=android.R.drawable.ic_menu_camera;

        public int getResId() {
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
                        if (null!=view&&null!=data&&data instanceof IPath){
                            return view instanceof ImageView?showPhoto(view,(IPath) data,"After photo tap click."):
                                    view instanceof CheckBox?choosePhoto(view,(IPath)data,"After photo tap click."):false;
                        }
                }
                return false;
            case 2:
                Context context=null!=view&&null!=data&&data instanceof IPath ?view.getContext():null;
                if (null!=context&&context instanceof Activity){
                    Intent intent=new Intent();
                    ArrayList<IPath> list=mChoose;
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

    private boolean choosePhoto(View view, IPath photo, String debug){
        if (null!=photo&&null!=view){
            List<IPath> choose=mChoose;
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

    private boolean showPhoto(View view, IPath photo, String debug){
        ArrayList<IPath> list=null!=view&&null!=photo&&photo instanceof IPath ?new ArrayList<>(1):null;
        return null!=list&&list.add((IPath)photo)&& PhotoPreviewActivity.start(view.getContext(),list,0,debug);
    }

    private boolean cleanChoose(boolean notify){
        ArrayList<IPath> list=mChoose;
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
