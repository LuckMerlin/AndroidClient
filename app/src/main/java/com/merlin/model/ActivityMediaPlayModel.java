package com.merlin.model;


import com.merlin.adapter.MediaPlayDisplayAdapter;

public class ActivityMediaPlayModel extends Model{
    private final MediaPlayDisplayAdapter mDisplayAdapter=new MediaPlayDisplayAdapter();

    public MediaPlayDisplayAdapter getDisplayAdapter() {
        return mDisplayAdapter;
    }
}
