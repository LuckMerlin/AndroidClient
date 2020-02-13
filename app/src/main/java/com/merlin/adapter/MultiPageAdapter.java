package com.merlin.adapter;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.debug.Debug;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import static com.merlin.api.What.WHAT_SUCCEED;

public abstract class MultiPageAdapter<D,T,M extends PageData<T>> extends  Adapter<T>  implements OnMoreLoadable{
    private Page<D> mCurrentPage;
    private Page<D> mLoadingPage;
    private WeakHashMap<OnPageLoadUpdate,Object> mUpdateListeners;

    public interface OnPageLoadUpdate{
        int UPDATE_PAGE_END=123;
        int UPDATE_PAGE_START=124;
        void onPageLoadUpdate(int state,boolean idle,Page page);
    }

    protected abstract boolean onPageLoad(D arg,int page, OnApiFinish<Reply<M>> finish);

    private final boolean fillPage(PageData<T> page){
        if (null==page){
            return false;
        }
        List<T> list=page.getData();
        return (page.getPage()<=0?setData(list):append(true,list));
    }

    public boolean resetLoad(String debug){
        Page<D> page=mCurrentPage;
        D arg=null!=page?page.mArg:null;
        mCurrentPage=null;
        empty();
        return loadPage(arg,debug);
    }

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
    public final boolean onLoadMore(RecyclerView recyclerView, int state, String debug) {
        return loadNextPage();
    }

    public final boolean loadNextPage(){
        Page<D> current=mCurrentPage;
        return !isLoading()&&null!=current&&loadPage(new Page<>(current.mArg,current.mPage+1));
    }

    public final boolean loadPage(D arg,String debug){
        Page<D> current=mCurrentPage;
        D currArg=null!=current?current.mArg:null;
        return loadPage(((null==arg&&null==currArg)||((null!=currArg&&null!=arg&&currArg.equals(arg)))
                ?new Page<>(currArg,(null==current?0:current.mPage+1)):new Page<>(arg,0)));
    }

    private boolean loadPage(Page<D> page){
        if (null!=page){
            final int pageIndex=page.mPage;
            Page<D> loading=mLoadingPage;
            if (isPageEquals(loading,page)){
                Debug.W(getClass(),"Not need load page while exist loading."+loading);
                return false;
            }
            mLoadingPage=page;
            notifyPageUpdate(OnPageLoadUpdate.UPDATE_PAGE_START,true,page);
            if(!onPageLoad(page.mArg,pageIndex,(what, note, data, arg)->{
                boolean idle=isPageEquals(mLoadingPage,page);
                notifyPageUpdate(OnPageLoadUpdate.UPDATE_PAGE_END,idle,page);
                if (idle){
                    mLoadingPage=null;
                    if (what== WHAT_SUCCEED){
                        mCurrentPage=page;
                        fillPage(null!=data?data.getData():null);
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
        private int mPage;
        private T mArg;

        private Page(T arg,int page){
            mArg=arg;
            mPage=page;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (null!=obj&&obj instanceof Page){
                Page page=(Page)obj;
                return mPage==page.mPage&&((null==mArg&&null==page.mArg)||(
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
