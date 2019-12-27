package com.merlin.model;

import android.content.Context;

import com.merlin.adapter.MediaListAdapter;

public class MediaPlayModel extends BaseModel{
    private final MediaListAdapter mPlayingAdapter=new MediaListAdapter();

    public MediaPlayModel(Context context){
        super(context);
    }

    public MediaListAdapter getPlayingAdapter() {
        return mPlayingAdapter;
    }
}
