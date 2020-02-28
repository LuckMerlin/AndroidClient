package com.merlin.util;

import com.merlin.bean.FileMeta;
import com.merlin.client.R;
import com.merlin.debug.Debug;

public final class DefaultThumbs {

    public static Object getDefaultThumbByExtension(String extension){
        return null;
    }

    public static Object getDefaultThumbByMeta(FileMeta meta){
        if (null!=meta){
            if (meta.isDirectory()){
                return R.drawable.folder;
            }
            final String extension=meta.getExtension();
            if (null==extension||extension.length()<=0){
                return null;
            }
            if (extension.equalsIgnoreCase(".jpg")||
                    extension.equalsIgnoreCase(".jpeg")||
                    extension.equalsIgnoreCase(".png")){
                return meta.getPath();
            }
            return getDefaultThumbByExtension(extension);
        }
        return null;
    }
}
