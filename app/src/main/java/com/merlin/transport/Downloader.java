package com.merlin.transport;

import android.content.Context;

import com.merlin.debug.Debug;

public final class Downloader extends Transporter{

    public Downloader(Context context){
        super(context);
    }

    public boolean download(Download download, boolean interactive, OnStatusChange change, String debug){
        final String path=null!=download?download.getFromPath():null;
        if (null==path||path.length()<=0){
            Debug.W(getClass(),"Can't upload file which path invalid."+path);
            notifyStatusChange(TRANSPORT_REMOVE,upload,progress);
            return false;
        }
        return false;
    }
}
