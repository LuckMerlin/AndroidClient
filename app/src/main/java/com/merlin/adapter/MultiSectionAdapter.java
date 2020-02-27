package com.merlin.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.api.SectionData;
import com.merlin.api.What;
import com.merlin.client.R;
import com.merlin.debug.Debug;

import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

public abstract class MultiSectionAdapter<D,T,M extends SectionData<T>> extends  Adapter<T>  implements OnMoreLoadable{
    private Page<D> mCurrentPage;
    private Page<D> mLoadingPage;
    private WeakHashMap<OnPageLoadUpdate,Object> mUpdateListeners;
    private M mLastPage;

    public interface OnPageLoadUpdate{
        int UPDATE_PAGE_END=123;
        int UPDATE_PAGE_START=124;
        void onPageLoadUpdate(int state, boolean idle, Page page);
    }

    protected abstract boolean onPageLoad(D arg,int page, OnApiFinish<Reply<M>> finish);

    private final boolean fillPage(M page){
        if (null==page){
            return false;
        }
        page.getLength();
        List<T> list=page.getData();
        int from=page.getFrom();
        int to=page.getTo();
        if (from<0||to<from){
            return false;
        }
        mLastPage=page;
        return replace(list,from);
    }

    @Override
    public final boolean empty() {
        boolean succeed=super.empty();
        mCurrentPage=null;
        mLastPage=null;
        return succeed;
    }

    public boolean reset(String debug){
        Page<D> page=mCurrentPage;
        D arg=null!=page?page.mArg:null;
        empty();
        return loadPage(arg,debug);
    }

    public final M getLastPage() {
        return mLastPage;
    }

    //    public boolean reloadVisible(String debug){
//
//        return false;
//    }

    public final Page<D> getLoadingPage() {
        return mLoadingPage;
    }

    public final boolean isLoading(){
        return null!=getLoadingPage();
    }

    public final D getLoadingPageArg(){
        Page<D> data=mLoadingPage;
        return null!=data?data.mArg:null;
    }

    @Override
    public final boolean onLoadMore(RecyclerView rv, int state, String debug) {
        if (isAllLoaded()){
            Context context=null!=rv?rv.getContext():null;
            if (null!=context){
                Toast.makeText(context, R.string.noMoreData,Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        if (state==LoadMoreInterceptor.TAIL){
            return loadNextPage(debug);
        }
        RecyclerView.LayoutManager manager=null!=rv?rv.getLayoutManager():null;
        if (null!=manager){
            if (manager instanceof LinearLayoutManager){
                LinearLayoutManager lm=(LinearLayoutManager)manager;
                int firstPosition=lm.findFirstVisibleItemPosition();
                View view=lm.findViewByPosition(firstPosition);
                int[] location = new int[2];
                int orientation=lm.getOrientation();
                if (orientation==LinearLayoutManager.VERTICAL){
                    rv.getLocationInWindow(location);
                    int rvTop=location[1];
                    if (null!=view) {
                        view.getLocationInWindow(location);
                        int viewTop = location[1] - view.getTop();
                        if (viewTop == rvTop){
                            return loadNextPage("After page not full " + (null != debug ? debug : "."));
                        }
                    }
                    return reset("After page pull down.");
                }else if (orientation==LinearLayoutManager.HORIZONTAL){
                    rv.getLocationInWindow(location);
                    int rvLeft=location[0];
                    if (null!=view) {
                        view.getLocationInWindow(location);
                        int viewLeft = location[0] - view.getLeft();
                        return viewLeft == rvLeft ? loadNextPage("After page not full " +
                                (null != debug ? debug : ".")) : reset("After page pull left.");
                    }
                }
            }else if (manager instanceof StaggeredGridLayoutManager){
//                ((StaggeredGridLayoutManager)manager).f
            }
        }
        return false;
    }

    public final boolean isAllLoaded(){
        Page<D> page=mCurrentPage;
        Integer total=null!=page?page.mTotal:null;
        if (null!=total){
            if(getDataSize()>=total){
                return true;
            }
        }
        return false;
    }

    public final boolean loadNextPage(String debug){
        Page<D> current=mCurrentPage;
        int size=getDataSize();
        return !isLoading()&&null!=current&&loadPage(new Page<>(current.mArg,size,null),debug);
    }

    public final boolean loadPage(D arg,String debug){
        Page<D> current=mCurrentPage;
        D currArg=null!=current?current.mArg:null;
        if ((null==arg&&null==currArg)||(null!=currArg&&null!=arg&&currArg.equals(arg))){
            return loadPage(new Page<>(arg,(null==current?0:current.mFrom+1),null),debug);
        }
        empty();
        return loadPage(new Page<>(arg,0,null),debug);
    }

    protected void onNoMoreData(M data){
        Debug.D(getClass(),"没有更多内容了");
    }

    private boolean loadPage(Page<D> page,String debug){
        if (null!=page){
            final int from=page.mFrom;
            Page<D> loading=mLoadingPage;
            if (isPageEquals(loading,page)){
                Debug.W(getClass(),"Not need load page while exist loading."+loading);
                return false;
            }
            mLoadingPage=page;
            notifyPageUpdate(OnPageLoadUpdate.UPDATE_PAGE_START,true,page);
            if(!onPageLoad(page.mArg,from,(what, note, data, arg)->{
                boolean idle=isPageEquals(mLoadingPage,page);
                notifyPageUpdate(OnPageLoadUpdate.UPDATE_PAGE_END,idle,page);
                if (idle){
                    mLoadingPage=null;
                    switch (what){
                        case What.WHAT_SUCCEED:
                            M m=null!=data?data.getData():null;
                            int total=null!=m?m.getLength():-1;
                            if (total>=0){
                                mCurrentPage=new Page<>(page.mArg,page.mFrom,total);
                                fillPage(m);
                            }
                            break;
                        case What.WHAT_OUT_OF_BOUNDS:
                            onNoMoreData(null!=data?data.getData():null);
                            break;
                    }
                } })){
                mLoadingPage=null;
                return false;
            }
            return true;
        }
        Debug.W(getClass(),"Can't load page data.page="+page);
        return false;
    }

    private boolean isPageEquals(Page<D> page1,Page<D>  page2){
        return (null==page1&&null==page2)||(null!=page1&&null!=page2&&page1.equals(page2));
    }

    public final static class Page<T> {
        private final int mFrom;
        private final Integer mTotal;
        private final T mArg;

        private Page(T arg,int from, Integer total){
            mArg=arg;
            mFrom=from;
            mTotal=total;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (null!=obj&&obj instanceof Page){
                Page page=(Page)obj;
                return mFrom==page.mFrom&&((null==mArg&&null==page.mArg)||(
                        mArg.equals(page.mArg)));
            }
            return super.equals(obj);
        }
    }

    public final boolean add(OnPageLoadUpdate update){
        if (null!=update){
            WeakHashMap<OnPageLoadUpdate,Object> reference=mUpdateListeners;
            reference=null!=reference?reference:(mUpdateListeners=new WeakHashMap<>(1));
            synchronized (reference){
                if (!reference.containsKey(update)){
                    reference.put(update,System.currentTimeMillis());
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    private void notifyPageUpdate(int state,boolean idle,Page page){
        WeakHashMap<OnPageLoadUpdate,Object> reference=mUpdateListeners;
        Set<OnPageLoadUpdate> set=null!=reference?reference.keySet():null;
        if (null!=set){
            synchronized (set){
                for (OnPageLoadUpdate update:set) {
                    if (null!=update){
                        update.onPageLoadUpdate(state,idle,page);
                    }
                }
            }
        }
    }
}
