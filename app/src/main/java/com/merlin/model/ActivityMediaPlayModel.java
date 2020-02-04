package com.merlin.model;

import com.merlin.adapter.MediaPlayDisplayAdapter;
import com.merlin.debug.Debug;

public class ActivityMediaPlayModel extends Model{
    private final MediaPlayDisplayAdapter mDisplayAdapter=new MediaPlayDisplayAdapter();


    public MediaPlayDisplayAdapter getDisplayAdapter() {
        Debug.D(getClass(),"QQQQQQQQQQQ "+mDisplayAdapter);
        return mDisplayAdapter;
    }
}
