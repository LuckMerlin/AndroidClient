package com.merlin.model;

import android.content.Context;
import com.merlin.adapter.MediaAdapter;
import com.merlin.media.Media;

import java.util.ArrayList;
import java.util.List;


public final class MediasAllModel extends BaseModel {
    private final MediaAdapter mAdapter=new MediaAdapter();

    public MediasAllModel(Context context){
        super(context);
        List<Media> list=new ArrayList<>();
        Media media=new Media();
        media.setTitle("我不愿让你一个人");
        media.setTitle("吴越");
        list.add(media);
        mAdapter.setData(list);
    }

    public MediaAdapter getAdapter() {
        return mAdapter;
    }
}
