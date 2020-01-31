package com.merlin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.merlin.adapter.MediaAdapter;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.bean.Media;

import java.util.ArrayList;
import java.util.List;


public class MediasAllLayout extends LinearLayout {
    public MediasAllLayout(@NonNull Context context) {
        this(context, null);
    }

    public MediasAllLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//        List<Media> list=new ArrayList<>();
//        Media media=new Media();
//        media.setTitle("我不愿让你一个人");
//        media.setArtist("吴越");
//        media.setAlbum("专辑");
//        for (int i=0;i<20;i++){
//            list.add(media);
//        }
//        mAdapter.setData(list);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }
}
