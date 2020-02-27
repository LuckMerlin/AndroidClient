package com.merlin.model;

import android.content.Context;

import com.merlin.bean.ClientMeta;

public class LocalBrowserModel extends BrowserModel {

    public LocalBrowserModel(Context context,ClientMeta meta,ClientCallback callback){
        super(context,meta,callback);
    }

}
