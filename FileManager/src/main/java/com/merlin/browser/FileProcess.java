package com.merlin.browser;

import android.content.Context;

import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Processing;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.Path;
import com.merlin.debug.Debug;
import com.merlin.file.R;
import com.merlin.lib.Cancel;
import com.merlin.lib.Canceler;
import com.merlin.retrofit.Retrofit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public abstract class FileProcess<T extends Path> extends ArrayList<T> implements Canceler{
    private final String mTitle;
    private boolean mProcessing=false;
    private boolean mCancel=false;
    private int mProcessingIndex;

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

    protected void onCanceled(boolean cancel,String debug){
        //Do nothing
    }

    public final boolean isProcessing() {
        return mProcessing;
    }

    @Override
    public final boolean cancel(boolean cancel, String debug) {
        if (mCancel!=cancel){
            mCancel=cancel;
            onCanceled(cancel, debug);
            return true;
        }
        return false;
    }

    public final boolean isCancel() {
        return mCancel;
    }

    protected abstract Reply onProcess(T pathObj, OnProcessUpdate update, Retrofit retrofit);

    public final Reply onProcess(OnProcessUpdate update, Retrofit retrofit) {
        if (null==update){
            Debug.W(getClass(),"Can't process file delete with invalid args "+update);
            return null;
        }
        if (!mProcessing){
            synchronized (this){
                if (size()>=0){
                    mProcessingIndex=0;
                    Reply reply=null;
                    mProcessing=true;
                    for (T pathObj:this) {
                        mProcessingIndex+=1;
                        reply=onProcess(pathObj,update,retrofit);
                        if (isCancel()){
                            reply=new Reply(true,What.WHAT_CANCEL,"Canceled.",null);
                            break;
                        }
                    }
                    mProcessing=false;
                    return reply;
                }
            }
        }
        return null;
    }

    public final int getProcessingIndex() {
        return mProcessingIndex;
    }

    public interface OnProcessUpdate {
        void onProcessUpdate(Object note, Path from, Path to, Path instant, Integer progress, List<Path> processed);
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
