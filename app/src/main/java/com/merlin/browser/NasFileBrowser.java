package com.merlin.browser;

import android.content.Context;
import android.view.View;

import com.merlin.api.Canceler;
import com.merlin.api.OnApiFinish;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.FileMeta;

public class NasFileBrowser extends FileBrowser {

    public NasFileBrowser(Context context, ClientMeta meta,Callback callback){
        super(context,meta,callback);
    }

    @Override
    protected Canceler onPageLoad(Object arg, int from, OnApiFinish finish) {
        return null;
    }

    @Override
    protected boolean onShowFileDetail(View view, FileMeta meta, String debug) {

        return false;
    }

    @Override
    protected boolean onSetAsHome(View view, String path, String debug) {

        return false;
    }
}
