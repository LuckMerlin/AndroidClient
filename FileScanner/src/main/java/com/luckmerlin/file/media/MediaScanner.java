package com.luckmerlin.file.media;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.util.Closer;
import com.luckmerlin.file.Callback;
import com.luckmerlin.core.match.Matchable;

import java.util.List;

public final class MediaScanner implements Callback{

    public boolean scanImages(Context context, boolean inner, List<MediaFile> files, Callback callback) {
        return scanImages(context,inner,files,callback,null);
    }

    public boolean scanImages(Context context, boolean inner, List<MediaFile> files,Callback callback, Matchable matchable) {
        return scanImages(context,inner,null,null,null,files,callback,matchable);
    }

    private boolean scanImages(Context context, boolean inner, String selection, String[] selectionArgs,String sort, List<MediaFile> files, Callback callback, Matchable matchable){
        Uri uri=inner?MediaStore.Images.Media.INTERNAL_CONTENT_URI:MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver=null!=context?context.getContentResolver():null;
        if (null==resolver){
            Debug.W("Can't scan images while resolver is NULL.");
            notifyScanFinish(ARGS_INVALID,"Context or resolver NULL.",uri,files,callback);
            return false;
        }
       Cursor cursor=null;
        try {
            cursor = resolver.query(uri, new String[]{MediaStore.Images.Media._ID,MediaStore.Images.Media.DATE_ADDED,
                            MediaStore.Images.Media.MIME_TYPE,MediaStore.Images.Media.ORIENTATION,
                            MediaStore.Images.Media.WIDTH,MediaStore.Images.Media.HEIGHT,MediaStore.Images.Media.TITLE,
                            MediaStore.Images.Media.LATITUDE,MediaStore.Images.Media.LONGITUDE,MediaStore.Images.Media.DISPLAY_NAME,
                            MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DATE_MODIFIED},selection, selectionArgs,
                    null!=sort?sort:MediaStore.Images.Media.DATE_MODIFIED);
            if (null==cursor){
                Debug.W("Can't scan images while cursor invalid.");
                notifyScanFinish(FAIL,"Cursor is NULL.",uri,files,callback);
                return false;
            }
            if (cursor.getCount()<=0){
                notifyScanFinish(SUCCEED,null,uri,files,callback);
                return false;
            }
            cursor.moveToFirst();
            do {
                long mediaId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                Uri mediaUri= Uri.withAppendedPath(uri, "" + mediaId);
                Debug.D("EEEEEEEEEEee "+mediaId+" "+mediaUri
                );
            }while (cursor.moveToNext());
            notifyScanFinish(SUCCEED,null,uri,files,callback);
           return true;
        }catch (Exception e){
            Debug.E("Exception scan images.e="+e,e);
            notifyScanFinish(EXCEPTION,"Exception scan images."+e,uri,files,callback);
            return false;
        }finally {
            new Closer().close(cursor);
        }
    }


    public boolean scanMedia(Context context,String[] paths){
//        MediaScannerConnection.scanFile(context,paths, new String[]{"image/" + ext},
//                new MediaScannerConnection.OnScanCompletedListener() {
//                    @Override
//                    public void onScanCompleted(final String path, Uri uri) {
//
//                    }
//                });
        return true;
    }


    private boolean notifyScanFinish(int what, String note, Uri src, List<MediaFile> files,Callback callback){
        if (null!=callback&&callback instanceof OnMediaScanFinish){
            ((OnMediaScanFinish)callback).onScanFinish(what,note,src,files);
            return true;
        }
        return false;
    }

}
