package com.merlin.photo;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

public class LocalPhotoLoader {
    private LoaderManager loaderManager;

    public interface OnLocalPhotoLoad{
        int WHAT_FINISH=2008;
        int WHAT_LOAD_ONE=2009;
        int WHAT_LOAD_CANCEL=2010;
        boolean onLocalPhotoLoaded(int what,Photo photo);
    }

    public final boolean load(Context context, OnLocalPhotoLoad callback){
        return load(null!=context?context.getContentResolver():null,callback);
    }

    public final boolean load(ContentResolver resolver,OnLocalPhotoLoad callback){
        return load(null!=resolver?resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, null):null,callback);
    }

    public final boolean load(Cursor cursor,OnLocalPhotoLoad callback){
        if (null!=cursor&&null!=callback){
            while (!cursor.isClosed()&&cursor.moveToNext()) {
                Photo photo=onLocalPhotoFromCursor(cursor);
                if (null!=photo){
                   if (callback.onLocalPhotoLoaded(OnLocalPhotoLoad.WHAT_LOAD_ONE,photo)){
                       break;
                   }
                }
                photo=onLocalPhotoFromCursor(cursor);
            }
            callback.onLocalPhotoLoaded(OnLocalPhotoLoad.WHAT_FINISH,photo);
        }
        return false;
    }

    protected Photo onLocalPhotoFromCursor(Cursor cursor){
        //           String[] names= cursor.getColumnNames();
//            for (String name:names) {
//                Debug.D(name);
//            }
        //                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
//                //获取图片的生成日期
//                byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//                //获取图片的详细信息
//                String desc = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DESCRIPTION));
//                names.add(name);
//                descs.add(desc);
//                fileNames.add(new String(data, 0, data.length - 1));
        return null;
    }

}
