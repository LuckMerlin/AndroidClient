package com.merlin.model;


import android.view.View;
import android.widget.ImageView;

import com.merlin.adapter.ListAdapter;
import com.merlin.adapter.PhotoGridAdapter;
import com.merlin.api.Canceler;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.bean.Path;
import com.merlin.client.R;

public abstract class PhotoGridModel extends Model {
    protected abstract Canceler onPageLoad(String arg, int from, OnApiFinish<Reply<PageData<Path>>> finish);

    private final PhotoGridAdapter mAdapter=new PhotoGridAdapter(){
        @Override
        protected Canceler onPageLoad(String arg, int from, OnApiFinish<Reply<PageData<Path>>> finish) {
            return PhotoGridModel.this.onPageLoad(arg,from,finish);
        }
    };

    @Override
    protected void onRootAttached(View root) {
        super.onRootAttached(root);
        ImageView imageView=new ImageView(getViewContext());
        imageView.setImageResource(R.drawable.album_default);
        mAdapter.setFixHolder(ListAdapter.TYPE_TAIL,imageView);
    }

    public final PhotoGridAdapter getAdapter(){
        return mAdapter;
    }
}
