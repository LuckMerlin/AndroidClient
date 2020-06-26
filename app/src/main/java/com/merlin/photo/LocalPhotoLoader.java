package com.merlin.photo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.merlin.debug.Debug;

public class LocalPhotoLoader {

    public interface OnLocalPhotoLoad{
        int WHAT_FINISH=2008;
        int WHAT_LOAD_ONE=2009;
        int WHAT_LOAD_CANCEL=2010;
        int WHAT_LOAD_OUT_OF_BOUNDS=2011;
        int WHAT_LOAD_ERROR=2012;
        boolean onLocalPhotoLoaded(int what, LocalPhoto photo, long length);
    }

    public final boolean load(Context context,int from,int limit, OnLocalPhotoLoad callback){
        return load(null!=context?context.getContentResolver():null,from,limit,callback);
    }

    public final boolean load(ContentResolver resolver,int from,int limit,OnLocalPhotoLoad callback){
//        String[] projection,String selection,String[] selectionArgs,String sortOrder
//        select ＊ from anyTable where var='const'
//        那么anyTable就是uri，＊就是projection，selection是“var=?"，selectionArgs写成这样：new String[]{'const‘｝
        Cursor cursor=resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,null, null, null);
        boolean succeed=load(cursor,from,limit,callback);
        if (null!=cursor) {
            cursor.close();
        }
        return succeed;
    }

    public final boolean load(Cursor cursor,int from,int limit,OnLocalPhotoLoad callback){
        long length=-1;
        if (null!=cursor&&null!=callback){
            if(from<0){
                callback.onLocalPhotoLoaded(OnLocalPhotoLoad.WHAT_LOAD_ERROR,null,-1);
                cursor.close();
                return false;
            }
            if (!cursor.isClosed()){
                length=cursor.getCount();
                if (from>=length){
                    callback.onLocalPhotoLoaded(OnLocalPhotoLoad.WHAT_LOAD_OUT_OF_BOUNDS,null,length);
                    cursor.close();
                    return false;
                }
                limit=limit<=0?10:limit;
                Debug.D(getClass(),"Loading local photo from "+from+" with limit "+limit);
                if (cursor.moveToPosition(from)){
                   do {
                       if (--limit<0){
                           break;
                       }
                       LocalPhoto photo=onLocalPhotoFromCursor(cursor);
                       if (null!=photo&&!callback.onLocalPhotoLoaded(OnLocalPhotoLoad.WHAT_LOAD_ONE,photo,length)){
                           callback.onLocalPhotoLoaded(OnLocalPhotoLoad.WHAT_LOAD_CANCEL,null,length);
                           break;
                       }
                   }while (cursor.moveToNext());
                }
            }
            cursor.close();
            callback.onLocalPhotoLoaded(OnLocalPhotoLoad.WHAT_FINISH,null,length);
            return true;
        }
        Debug.W(getClass(),"Can't load local photo from cursor with NULL args. "+cursor+" "+callback);
        return false;
    }

    protected LocalPhoto onLocalPhotoFromCursor(Cursor cursor){
         if (null!=cursor&&!cursor.isClosed()){
             String title=getCursorString(cursor,MediaStore.Images.Media.TITLE,null);
             String desc = getCursorString(cursor,MediaStore.Images.Media.DESCRIPTION,null);
//             String  = getCursorString(cursor,MediaStore.Images.Media.DATE_TAKEN);
             String  mimeType= getCursorString(cursor,MediaStore.Images.Media.MIME_TYPE,null);
             String  path= getCursorString(cursor,MediaStore.Images.Media.DATA,null);
             int  width= getCursorInt(cursor,MediaStore.Images.Media.WIDTH,0);
             int  height= getCursorInt(cursor,MediaStore.Images.Media.HEIGHT,0);
             return new LocalPhoto(path,title,mimeType,width,height,desc);
         }
        Debug.W(getClass(),"Can't load local photo from cursor. "+cursor);
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

    public final static class LocalPhoto{
        private final String mPath;
        private final String mTitle;
        private final String mMimeType;
        private final int mWidth;
        private final int mHeight;
        private final String mDesc;

        private LocalPhoto(String path,String title,String mimeType,int width,int height,String desc){
            mPath=path;
            mTitle=title;
            mMimeType=mimeType;
            mWidth=width;
            mHeight=height;
            mDesc=desc;
        }

        public int getHeight() {
            return mHeight;
        }

        public int getWidth() {
            return mWidth;
        }

        public String getDesc() {
            return mDesc;
        }

        public String getMimeType() {
            return mMimeType;
        }

        public String getPath() {
            return mPath;
        }

        public String getTitle() {
            return mTitle;
        }
    }

}
