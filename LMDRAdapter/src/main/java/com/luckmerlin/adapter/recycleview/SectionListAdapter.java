package com.luckmerlin.adapter.recycleview;
import android.view.ViewParent;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.luckmerlin.adapter.OnSectionLoadFinish;
import com.luckmerlin.core.Canceler;
import com.luckmerlin.core.debug.Debug;

import java.util.List;

public class SectionListAdapter<A,T> extends ListAdapter<T> {
    private SectionRequest<A> mLoadingNext, mLoadingPre;
    private LastSection<A> mPreLastSection, mNextLastSection;
    private boolean mAutoLoad = true;
    private boolean mDetachCancel = true;

    private final DragLoadMoreTouchListener mListener = new DragLoadMoreTouchListener() {
        @Override
        protected void onDragMore(RecyclerView rv, boolean next, float shiftX, float shiftY) {
            if (next){
                loadNextSection(null,false,"While drag load more.");
            }else{
                loadPreSection(null,false,"While drag load more.");
            }
        }
    };

    protected void onLoadStatusChange(SectionRequest<A> request,boolean loading,boolean next){
        //Do nothing
    }

    protected Canceler onPreSectionLoad(SectionRequest<A> request,OnSectionLoadFinish<A,T> callback,String debug){
        //Do nothing
        return null;
    }

    protected Canceler onNextSectionLoad(SectionRequest<A> request,
                                         OnSectionLoadFinish<A,T> callback,String debug){
        //Do nothing
        return null;
    }

    private void onSectionLoadStatusChange(SectionRequest<A> request,final boolean loading,boolean next){
        if (null!=request&&!request.isNext()){
            RecyclerView recyclerView=getRecyclerView();
            ViewParent parent=null!=recyclerView?recyclerView.getParent():null;
            if (null!=parent&&parent instanceof SwipeRefreshLayout){
                final SwipeRefreshLayout layout=((SwipeRefreshLayout)parent);
                layout.post(new Runnable() {
                    @Override
                    public void run() {
                        layout.setRefreshing(loading);
                    }
                });
            }
        }
        onLoadStatusChange(request,loading,next);
    }

    public SectionListAdapter() {
        this(true, true);
    }

    public SectionListAdapter(boolean autoLoad, boolean detachCancel) {
        mAutoLoad = autoLoad;
        mDetachCancel = detachCancel;
    }

    protected void onAttachRecyclerView(RecyclerView recyclerView) {
        //Do nothing
    }

    protected void onDetachRecyclerView(RecyclerView recyclerView) {
        //Do nothing
    }

    public final boolean isAllLoaded(boolean next){
        LastSection section=next?mNextLastSection:mPreLastSection;
        return null!=section&&section.mLength==section.mSize;
    }

    public final boolean resetSection(String debug) {
        return resetSection(null,debug);
    }

    public final boolean resetSection(OnSectionLoadFinish<A,T> finalCallback,String debug) {
        return resetSection(null, null,finalCallback, debug);
    }

    public final boolean resetSection(A arg, Boolean insert,final   OnSectionLoadFinish<A,T> finalCallback,String debug) {
        //If exist loading request,cancel it
        cancelLoading("Before reset section " + (null != debug ? debug : "."));
        super.clean(debug);
        mPreLastSection=mNextLastSection=null;
        return loadNextSection(arg, insert, new OnSectionLoadFinish<A, T>() {
            @Override
            public void onSectionLoadFinish(boolean succeed, String note, Section<A, T> section) {
                if (null!=finalCallback){
                    finalCallback.onSectionLoadFinish(succeed,note,section);
                }
                onReset(succeed,section);
            }}, debug);
    }

    protected void onReset(boolean succeed,Section<A,T> section) {
        //Do nothing
    }

    public final A getLatestSectionArg(){
        LastSection<A> nextLastSection=mNextLastSection;
        LastSection<A> preLastSection=mPreLastSection;
        long nextLastTime=null!=nextLastSection?nextLastSection.mTime:0;
        long preLastTime=null!=preLastSection?preLastSection.mTime:0;
        LastSection<A> section= nextLastTime>preLastTime?nextLastSection:preLastSection;
        return null!=section?section.mArg:null;
    }

    public final LastSection<A> getNextLastSection() {
        return mNextLastSection;
    }

    public final LastSection<A> getPreLastSection() {
        return mPreLastSection;
    }

    public final long getPreLastSectionSize(){
        LastSection section=mPreLastSection;
        return null!=section?section.mSize:-1;
    }

    public final A getNextLastSectionArg(){
        LastSection<A> section=mNextLastSection;
        return null!=section?section.mArg:null;
    }

    public final A getPreLastSectionArg(){
        LastSection<A> section=mPreLastSection;
        return null!=section?section.mArg:null;
    }

    public final long getNextLastSectionSize(){
        LastSection section=mNextLastSection;
        return null!=section?section.mSize:-1;
    }

    public final boolean loadPreSection(A arg,Boolean insert, String debug) {
        if (null != mLoadingPre) {//Exist loading
            Debug.W("Fail load pre section while exist pre loading " + (null != debug ? debug : "."));
            return false;
        }else if (isAllLoaded(false)){
            return false;//All already loaded
        }
        LastSection<A> section=mPreLastSection;
        return loadSection(new SectionRequest<A>(null!=section?section.mArg:arg,
                null!=section?section.mSize:0, false), insert, debug);
    }

    final boolean loadNextSection(A arg, String debug) {
        return loadNextSection(arg,false,debug);
    }

    final boolean loadNextSection(A arg,Boolean insert,String debug) {
        return loadNextSection(arg,insert,null,debug);
    }

    final boolean loadNextSection(A arg,Boolean insert, OnSectionLoadFinish<A,T> finalCallback,String debug) {
        if (null != mLoadingNext) {//Exist loading
            Debug.W("Fail load next section while exist next loading " + (null != debug ? debug : "."));
            return false;
        }else if (isAllLoaded(true)){
            return false;//All already loaded
        }
        LastSection<A> section=mNextLastSection;
        return loadSection(new SectionRequest<A>(null!=section?section.mArg:arg,
                null!=section?section.mSize:0, true), insert,finalCallback, debug);
    }

    synchronized boolean loadSection(final SectionRequest<A> request, Boolean insert,String debug) {
        return loadSection(request,insert,null,debug);
    }

    synchronized boolean loadSection(final SectionRequest<A> request, Boolean insert,final OnSectionLoadFinish<A,T> finalCallback,String debug) {
        if (null != request) {
            final boolean actionInsert = null != insert && insert;
            if (request.isNext()) {
                if (null == mLoadingNext) {
                    mLoadingNext = request;
                    final OnSectionLoadFinish callback=new OnSectionLoadFinish<A,T>() {
                        @Override
                        public void onSectionLoadFinish(boolean succeed, String note, Section<A,T> section) {
                            SectionRequest current = mLoadingNext;
                            if (null != current && current == request) {
                                //Clean while load fail
                                current.mCanceler=null;
                                mLoadingNext = null;
                                List<T> data = succeed && null != section ? section.getData() : null;
                                int size=succeed&&null!=data?data.size():-1;
                                if (size > 0) {
                                    LastSection lastSection=new LastSection(request.getArg(),
                                            request.getFrom()+size,section.getTotal(),System.currentTimeMillis());
                                    mNextLastSection=(actionInsert?  insert(getDataCount(), data,
                                            false, "After load section succeed."):
                                            add(data, "After load section succeed."))?lastSection:mNextLastSection;
                                }
                                onSectionLoadStatusChange(request,false,true);
                            }
                            if (null!=finalCallback){
                                finalCallback.onSectionLoadFinish(succeed,note,section);
                            }
                        }
                    };
                    onSectionLoadStatusChange(request,true,true);
                    if (null != (request.mCanceler = onNextSectionLoad(request,callback, debug))) {
                        return true;
                    }
                    callback.onSectionLoadFinish(false,"Fail.",null);//Clean while fail
                    return false;
                }
            } else {
                if (null == mLoadingPre) {
                    mLoadingPre = request;
                    final OnSectionLoadFinish callback= new OnSectionLoadFinish<A,T>() {
                        @Override
                        public void onSectionLoadFinish(boolean succeed, String note, Section<A,T> section) {
                            SectionRequest current = mLoadingPre;
                            if (null != current && current == request) {
                                current.mCanceler=null;
                                mLoadingPre = null;
                                List<T> data = succeed && null != section ? section.getData() : null;
                                int size=succeed&&null!=data?data.size():-1;
                                if (size> 0) {
                                    LastSection lastSection=new LastSection(request.getArg(),
                                            request.getFrom()+size,section.getTotal(),System.currentTimeMillis());
                                    mPreLastSection=(actionInsert?  insert(getDataCount(), data,
                                            false, "After load section succeed."):
                                            add(data, "After load section succeed."))?lastSection:mPreLastSection;
                                }
                                onSectionLoadStatusChange(request,false,false);
                            }//Clean while load fail
                        }
                    };
                    onSectionLoadStatusChange(request,true,false);
                    if (null != (request.mCanceler = onPreSectionLoad(request, callback, debug))) {
                        return true;
                    }
                    callback.onSectionLoadFinish(false,"Fail.",null);//Clean while fail
                    return false;
                }
            }
        }
        return false;
    }

    public final boolean cancelLoading(String debug) {
        return cancelNextLoading(debug) | cancelPreLoading(debug);
    }

    public synchronized final boolean cancelPreLoading(String debug) {
        SectionRequest preloading = mLoadingPre;
        mLoadingPre = null;
        if (null != preloading) {
            Debug.D("Cancel pre section loading " + (null != debug ? debug : "."));
            return preloading.cancel(true, debug);
        }
        return false;
    }

    public final boolean cancelNextLoading(String debug) {
        SectionRequest nextLoading = mLoadingNext;
        mLoadingNext = null;
        if (null != nextLoading) {
            Debug.D("Cancel next section loading " + (null != debug ? debug : "."));
            return nextLoading.cancel(true, debug);
        }
        return false;
    }

    public final boolean isAutoLoad() {
        return mAutoLoad;
    }

    public final boolean isDetachCancel() {
        return mDetachCancel;
    }

    @Override
    protected final void onAttachedRecyclerView(RecyclerView recyclerView) {
        super.onAttachedRecyclerView(recyclerView);
        if (null != recyclerView) {
            recyclerView.addOnItemTouchListener(mListener);
        }
        onAttachRecyclerView(recyclerView);
        if (mAutoLoad) {
            resetSection("While attached recycle view.");
        }
    }

    @Override
    protected final void onDetachedRecyclerView(RecyclerView recyclerView) {
        super.onDetachedRecyclerView(recyclerView);
        if (null != recyclerView) {
            recyclerView.removeOnItemTouchListener(mListener);
        }
        cancelLoading("While adapter detached recycler view.");
        onDetachRecyclerView(recyclerView);
    }

    protected final boolean notifySectionLoadFinish(boolean succeed, String note, Section<A,T> section,OnSectionLoadFinish<A,T> callback){
        if (null!=callback){
            callback.onSectionLoadFinish(succeed,note,section);
            return true;
        }
        return false;
    }

    protected final Canceler generateCanceler(final Canceler...cancelers){
        return new Canceler(){
            @Override
            public boolean cancel(boolean b, String s) {
                if (null!=cancelers&&cancelers.length>0){
                    for (Canceler canceler:cancelers) {
                        if (null!=canceler){
                            canceler.cancel(b,s);
                        }
                    }
                }
                return true;
            }
        };
    }

    static class LastSection<T> {
        private final T mArg;
        private final long mSize;
        private final long mLength;
        private final long mTime;

        LastSection(T arg,long size,long length,long time){
            mArg=arg;
            mSize=size;
            mLength=length;
            mTime=time;
        }

        public long getTime() {
            return mTime;
        }
    }

}
