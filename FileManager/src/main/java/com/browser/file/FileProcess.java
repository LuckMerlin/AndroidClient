package com.browser.file;

import android.content.Context;

import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.Path;
import com.merlin.debug.Debug;
import com.merlin.file.R;
import com.merlin.lib.Canceler;
import com.merlin.retrofit.Retrofit;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;

public abstract class FileProcess<T extends Path> extends ArrayList<T> implements Canceler{
    private final String mTitle;
    private boolean mProcessing=false;
    private boolean mCancel=false;
    private int mProcessingIndex;

    public FileProcess(){
        this(null,null);
    }

    public FileProcess(String title, ArrayList<T> files){
        mTitle=title;
        addAll(null!=files&&files.size()>0?files:new ArrayList<>(0));
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

    public final boolean isCanceled() {
        return mCancel;
    }

    protected abstract Reply onProcess(T pathObj, ProcessProgress update, Retrofit retrofit);

    public final Reply onProcess(ProcessProgress update, Retrofit retrofit) {
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
                        if (isCanceled()){
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

    public final String getMessage(Context context){
        final int size=size();
        Path first=size>0?get(0):null;
        String name=null!=first?first.getName(false):null;
        return ""+(size==1?(null!=name?(""+getText(context,first.isDirectory()? R.string.folder:
                R.string.file)+" "+name):""):getText(context,R.string.items,size));
    }

    public final String getTitle() {
        return mTitle;
    }

    protected final void notify(String note, Path instant, Float progress, ProcessProgress update){
        if (null!=update){
            update.onFileActionProgressChange(note,instant,progress);
        }
    }

    protected final boolean close(Closeable ...closeables){
        if (null!=closeables&&closeables.length>0){
            for (Closeable child:closeables) {
                if (null!=child){
                    try {
                        child.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    protected final String getText(Context context, int textResId, Object ...args){
        return null!=context?context.getResources().getString(textResId,args):null;
    }
}
