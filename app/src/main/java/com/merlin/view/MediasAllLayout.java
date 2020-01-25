package com.merlin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Adapter;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.merlin.adapter.MediaAdapter;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.media.Media;
import com.merlin.model.DataListModel;

import java.util.ArrayList;
import java.util.List;


public class MediasAllLayout extends LinearLayout {
    private final MediaAdapter mAdapter=new MediaAdapter();

    public MediasAllLayout(@NonNull Context context) {
        this(context, null);
    }

    public MediasAllLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        List<Media> list=new ArrayList<>();
        Media media=new Media();
        media.setTitle("我不愿让你一个人");
        media.setArtist("吴越");
        media.setAlbum("专辑");
        for (int i=0;i<20;i++){
            list.add(media);
        }
        mAdapter.setData(list);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        RecyclerView rv=findViewById(R.id.medias_all_RV);
        if (null!=rv){
            rv.setAdapter(mAdapter);
            rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                       if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                           int  lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                           RecyclerView.Adapter adapter=rv.getAdapter();
                           if(null!=adapter&&lastVisibleItemPosition + 1 == adapter.getItemCount()){
                                Debug.D(getClass(),"@@@@@@@ 加载更多");
                           }
                       }
                    }
                }
            });
        }
        SwipeRefreshLayout layout=findViewById(R.id.medias_all_SRL);
        if (null!=layout){

        }
    }
}
