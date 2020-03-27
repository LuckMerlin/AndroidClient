package com.merlin.util;

import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.webkit.MimeTypeMap;

import com.merlin.debug.Debug;

import java.io.File;
import java.util.HashMap;

public class FileMeta {

    public final String getMimeType (File file) {
        String path=null != file && file.exists() && file.canRead() && file.isFile()?file.getAbsolutePath():null;
        if (null != path&&path.length()>0) {
            return MimeTypeMap.getFileExtensionFromUrl(path);
        }
        return null;
    }


//            MediaMetadataRetriever mmr=null;
//            try {
//                mmr = new MediaMetadataRetriever();
//                file.toURI();
////                ThumbnailUtils.
////                mmr.setDataSource(file.getAbsolutePath(),new HashMap<String, String>());
//                return mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
//            } catch (Exception e) {
//                Debug.E(getClass(),"AAAA "+e,e);
//            }finally {
//                if (null!=mmr){
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        mmr.close();
//                    }
//                }
//            }

}
