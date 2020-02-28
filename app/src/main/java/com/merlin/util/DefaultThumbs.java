package com.merlin.util;

import com.merlin.bean.FileMeta;
import com.merlin.client.R;

public final class DefaultThumbs {

    public static Object getDefaultThumbByExtension(String extension){
        return null;
    }

    public static Object getDefaultThumbByMeta(FileMeta meta){
        return null!=meta?meta.isDirectory()? R.drawable.folder:getDefaultThumbByExtension(meta.getExtension()):null;
    }
}
