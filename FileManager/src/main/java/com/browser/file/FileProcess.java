package com.browser.file;

import android.content.Context;

import com.merlin.api.OnProcessChange;
import com.merlin.api.ProcessingFetcher;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.Path;
import com.merlin.browser.Collector;
import com.merlin.debug.Debug;
import com.merlin.file.R;
import com.merlin.lib.Canceler;
import com.merlin.retrofit.Retrofit;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class FileProcess<T extends Path> extends FileAction{
    private Map<T,Reply> mFileMap;
    private final String mTitle;
    private T mProcessing=null;
    private List<Reply<Path>> mProcessed;

    public FileProcess(){
        this(null,null);
    }

    public FileProcess(String title, ArrayList<T> files){
        mTitle=title;
        addAll(files);
    }

    public final boolean addAll(Collection<T> collection){
        int size=null!=collection?collection.size():-1;
        if (size>0){
            Map<T,Reply> list=mFileMap;
            list=null==list?(mFileMap=new LinkedHashMap<>(size)):list;
            for (T child:collection) {
                if (null!=child){
                    list.put(child,null);
                }
            }
            return true;
        }
        return false;
    }

    public final boolean isProcessing() {
        return null!=mProcessing;
    }

    protected abstract Reply<Path> onProcess(T pathObj, OnProcessChange<T> processProgress, Retrofit retrofit);

    public final Reply<Path> onProcess(OnProcessChange<T> update, Retrofit retrofit) {
        if (null!=mProcessing){//Already processing
            return new Reply<>(true,What.WHAT_ALREADY_DOING,"Already processing",null);
        }
        final Reply[] oneFailReply=new Reply[1];
        final T next=randomNextUnFinish(oneFailReply);
        if (null==next){
            Reply reply=null!=oneFailReply&&oneFailReply.length>0?oneFailReply[0]:null;
            Debug.W(getClass(),"All process file finished."+size());
            return null!=reply?reply:new Reply<>(true,What.WHAT_ALREADY_DONE,"All process file finished",null);
        }
        mProcessing=next;
        notify(null,"Processing ",next,null,update);
        Reply<Path> childReply=onProcess(next,update,retrofit);
        childReply=null!=childReply?childReply:new Reply<>(true,What.WHAT_ERROR,"Unknown process fail", null);
        mProcessing=null;
        switch (childReply.getWhat()){
            case What.WHAT_CANCEL://Cancel reply,Give up process next
            case What.WHAT_INTERRUPT://Interrupt reply,Give up process next
                return childReply;
        }
        Map<T,Reply> map=mFileMap;
        if (null!=map){
            synchronized (map){
                if (map.containsKey(next)){
                    map.put(next,childReply);
                }
            }
        }
        notify(null,"Processed",next,null,update);
        return onProcess(update,retrofit);
    }

    public final T randomNextUnFinish(Reply[] oneFailReply){
        Map<T,Reply> map=mFileMap;
        if (null!=map){
            synchronized (map){
                Set<T> set=map.keySet();
                if (null!=set){
                    Reply childReply=null;
                    for (T child:set) {
                        if (null!=child){
                            childReply=map.get(child);
                            if (null==childReply){
                                return child;
                            }
                            if (null!=oneFailReply&&oneFailReply.length>0&&childReply.getWhat()!=What.WHAT_SUCCEED){
                                oneFailReply[0]=childReply;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public final T getAnyone(){
        Map<T,Reply> map=mFileMap;
        if (null!=map){
            synchronized (map){
                Set<T> set=map.size()>0?map.keySet():null;
                if (null!=set){
                    for (T child:set) {
                        if (null!=child){
                            return child;
                        }
                    }
                }
            }
        }
        return null;
    }

    public final int getProcessedCount() {
        int count=-1;
        Map<T,Reply> list=mFileMap;
        if (null!=list){
            synchronized (list){
               Set<T> set= list.keySet();
               if (null!=set&&set.size()>0){
                   count=0;
                   for (T child:set) {
                       count+=null!=child&&null!=list.get(child)?1:0;
                   }
               }
            }
        }
        return count ;
    }

    public final String getMessage(Context context){
        final int size=size();
        Path first=size>0?getAnyone():null;
        String name=null!=first?first.getName(false):null;
        return ""+(size==1?(null!=name?(""+getText(context,first.isDirectory()? R.string.folder:
                R.string.file)+" "+name):""):getText(context,R.string.items,size));
    }

    public final int size(){
        Map<T,Reply> list=mFileMap;
        return null!=list?list.size():-1;
    }

    public final String getTitle() {
        return mTitle;
    }

    protected final String getText(Context context, int textResId, Object ...args){
        return null!=context?context.getResources().getString(textResId,args):null;
    }
}
