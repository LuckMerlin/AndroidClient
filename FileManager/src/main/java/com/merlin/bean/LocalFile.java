package com.merlin.bean;

import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.file.R;

public class LocalFile extends Path {
    private transient Reply<Path> mSync;

    public int syncColor(){
        int color= R.color.syncNull;
        String md5=getMd5();
        if (null!=md5&&md5.length()>0){
            color=R.color.syncNeed;
            Reply<Path> sync=mSync;
            if (null!=sync){
                color=R.color.syncFail;
                if (sync.isSuccess()&&sync.getWhat()== What.WHAT_SUCCEED){
                    Path syncUrl=sync.getData();
                    String path=null!=syncUrl?syncUrl.getPath():null;
                    color=null!=path&&path.length()>=0?R.color.synced:R.color.syncedNone;
                }
            }
        }
        return color;
    }

    public Reply<Path> getSync() {
        return mSync;
    }
}
