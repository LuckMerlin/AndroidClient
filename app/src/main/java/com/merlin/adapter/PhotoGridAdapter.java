package com.merlin.adapter;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.bean.LocalPhoto;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemPhotoGridBinding;
import com.merlin.photo.Photo;

import java.util.List;

public abstract class PhotoGridAdapter extends PageAdapter<String, Photo>{
    private int mSpanCount;
    private boolean mVisibleCamera;

    public PhotoGridAdapter(){
        this(3,true);
    }

    public PhotoGridAdapter(int spanCount,boolean visibleCamera){
        mSpanCount=spanCount;
        mVisibleCamera=visibleCamera;
    }

    @Override
    protected Integer onResolveViewTypeLayoutId(int viewType) {
        return viewType==TYPE_DATA||viewType==TYPE_EMPTY||viewType==TYPE_TAIL? R.layout.item_photo_grid:null;
    }

    public final boolean visibleCamera(Boolean visible){
        if (null!=visible){
            boolean curr=mVisibleCamera;
            if (curr!=visible){
                mVisibleCamera=curr;
                notifyDataSetChanged();
                return true;
            }
        }
        return mVisibleCamera;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType, ViewDataBinding binding, int position, Photo data, @NonNull List<Object> payloads) {
        if (null!=binding&&binding instanceof ItemPhotoGridBinding){
            data=mVisibleCamera&&(viewType==TYPE_EMPTY||viewType==TYPE_TAIL)?new CameraPhoto():data;
            ((ItemPhotoGridBinding)binding).setPhoto(data);
        }
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        int spanCount=mSpanCount;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(rv.getContext(), spanCount<=0?1:spanCount);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rv.addItemDecoration(new GridSpacingItemDecoration(spanCount, 10, true));
        return gridLayoutManager;
    }

    private final class CameraPhoto implements Photo{
        @Override
        public Object getLoadUrl() {
            return android.R.drawable.ic_menu_camera;
        }
    }
}
