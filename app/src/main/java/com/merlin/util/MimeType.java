package com.merlin.util;

import android.media.MediaMetadataRetriever;
import android.os.Build;

import java.io.File;

public class MimeType {

    public final String getFileMimeType (File file) {
        String path=null != file && file.exists() && file.canRead() && file.isFile()?file.getAbsolutePath():null;
        if (null != path&&path.length()>0) {
            MediaMetadataRetriever mmr=null;
            try {
                mmr = new MediaMetadataRetriever();
                mmr.setDataSource(file.getAbsolutePath());
                return mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (Exception e) {
            }finally {
                if (null!=mmr){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        mmr.close();
                    }
                }
            }
        }
        return null;
    }
}
