package com.merlin.browser;

import android.content.Context;
import com.merlin.api.OnApiFinish;
import com.merlin.bean.Path;
import com.merlin.file.R;
import com.merlin.lib.Canceler;
import com.merlin.retrofit.Retrofit;

import java.util.ArrayList;

public abstract class FileProcess<T extends Path> extends ArrayList<T> {
    private final String mTitle;

    public static class Interrupt{
        private int mWhat;

        public boolean setWhat(int what){
            mWhat=what;
            notifyAll();
            return true;
        }

        public int getWhat() {
            return mWhat;
        }

        //        boolean onNext(int what);
    }

    public FileProcess(){
        this(null,null);
    }

    public FileProcess(String title, ArrayList<T> files){
        mTitle=title;
            if (null!=files&&files.size()>0){
                addAll(files);
            }
    }


    protected abstract Canceler onProcess(OnProcessUpdate update, OnApiFinish apiFinish, Retrofit retrofit) ;

    public interface OnProcessUpdate {
        void onProcessUpdate(int what, Object note, Path from, Path to, Object arg);
    }

    public String getMessage(Context context){
        final int size=size();
        Path first=size>0?get(0):null;
        String name=null!=first?first.getName(false):null;
        return ""+(size==1?(null!=name?(""+getText(context,first.isDirectory()? R.string.folder:
                R.string.file)+" "+name):""):getText(context,R.string.items,size));
    }

    public final String getTitle() {
        return mTitle;
    }

    protected final String getText(Context context, int textResId, Object ...args){
        return null!=context?context.getResources().getString(textResId,args):null;
    }
}
