package com.merlin.photo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.lk.debug.Debug;

public class LocalPhotoLoader {

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
                if (null!=photo&&callback.onLocalPhotoLoaded(OnLocalPhotoLoad.WHAT_LOAD_ONE,photo)){
                    callback.onLocalPhotoLoaded(OnLocalPhotoLoad.WHAT_LOAD_CANCEL,null);
                    break;
                }
            }
            callback.onLocalPhotoLoaded(OnLocalPhotoLoad.WHAT_FINISH,null);
            return true;
        }
        return false;
    }

    protected Photo onLocalPhotoFromCursor(Cursor cursor){
         if (null!=cursor&&!cursor.isClosed()){
             String title=getCursorString(cursor,MediaStore.Images.Media.TITLE,null);
             String desc = getCursorString(cursor,MediaStore.Images.Media.DESCRIPTION,null);
//             String  = getCursorString(cursor,MediaStore.Images.Media.DATE_TAKEN);
             String  mimeType= getCursorString(cursor,MediaStore.Images.Media.MIME_TYPE,null);
             String  path= getCursorString(cursor,MediaStore.Images.Media.DATA,null);
             int  width= getCursorInt(cursor,MediaStore.Images.Media.WIDTH,0);
             int  height= getCursorInt(cursor,MediaStore.Images.Media.HEIGHT,0);
             Debug.D(""+path+" "+title+" "+mimeType+" "+width+" "+height);
             return new Photo(path,title,mimeType,width,height,desc);
         }
        return null;
    }

    private String getCursorString(Cursor cursor,String key,String def){
        int index=null!=key&&key.length()>0&&null!=cursor&&!cursor.isClosed()?cursor.getColumnIndex(key):-1;
        return index>=0?cursor.getString(index):def;
    }

    private int getCursorInt(Cursor cursor,String key,int def){
        int index=null!=key&&key.length()>0&&null!=cursor&&!cursor.isClosed()?cursor.getColumnIndex(key):-1;
        return index>=0?cursor.getInt(index):def;
    }

}
