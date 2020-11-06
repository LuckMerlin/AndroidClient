package com.luckmerlin.adapter.recycleview;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luckmerlin.core.Canceler;
import com.luckmerlin.core.debug.Debug;

public abstract class SectionListAdapter<T> extends ListAdapter<T> {
    private SectionRequest mLoading;
    private boolean mAutoReset=true;
    private final ScrollListener mListener=new ScrollListener();

    protected abstract Canceler onSectionLoad(SectionRequest request, OnSectionLoadFinish callback);

    protected void onAttachRecyclerView(RecyclerView recyclerView){
       //Do nothing
   }

    protected void onDetachRecyclerView(RecyclerView recyclerView){
        //Do nothing
    }

    public final boolean resetSection(String debug){
        
        return false;
    }

    @Override
    public final void onAttachedRecyclerView(RecyclerView recyclerView) {
        super.onAttachedRecyclerView(recyclerView);
        if (null!=recyclerView){
            recyclerView.addOnScrollListener(mListener);
        }
        onAttachRecyclerView(recyclerView);
        if (mAutoReset) {
            resetSection("While attached recycle view.");
        }
    }

    @Override
    public final void onDetachedRecyclerView(RecyclerView recyclerView) {
        super.onDetachedRecyclerView(recyclerView);
        if (null!=recyclerView){
            recyclerView.removeOnScrollListener(mListener);
        }
        onDetachRecyclerView(recyclerView);
    }


    private static class ScrollListener extends RecyclerView.OnScrollListener{

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (null!=recyclerView&&newState == RecyclerView.SCROLL_STATE_IDLE) { //当前状态为停止滑动
                if (!recyclerView.canScrollVertically(1)) { // 到达底部
                    Debug.D("WWWWWWWWWWWWWWw 底部");
//                    Toast.makeText(SessionActivity.this, "到达底部", Toast.LENGTH_SHORT).show();
                } else if (!recyclerView.canScrollVertically(-1)) { // 到达顶部
                    Debug.D("WWWWWWWWWWWWWWw 顶部");
//                    Toast.makeText(SessionActivity.this, "到达顶部", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    //    public final boolean empty() {
//        boolean succeed=super.clean();
//        mCurrentPage=null;
//        mLastPage=null;
//        return succeed;
//    }
//
//    public boolean resetAdapter(String debug){
//        Page<D> page=mCurrentPage;
//        Debug.D("Reset current page "+page+" "+(null!=debug?debug:"."));
//        D arg=null!=page?page.mArg:null;
//        return loadPage(new Page<>(arg,0,null),true,debug);
//    }
//
//    public final boolean isCurrentArgEquals(D arg){
//        Page<D> page=mCurrentPage;
//        D currArg=null!=page?page.mArg:null;
//        return (null==currArg&&null==arg)||(null!=currArg&&null!=arg&&currArg.equals(arg));
//    }
//
//    public final boolean isLoadingArgEquals(D arg){
//        Page<D> page=mLoadingPage;
//        D loadingArg=null!=page?page.mArg:null;
//        return (null==loadingArg&&null==arg)||(null!=loadingArg&&null!=arg&&loadingArg.equals(arg));
//    }
//
//    public final Page<D> getCurrentPage() {
//        return mCurrentPage;
//    }
//
//    public final PageData<T> getLastPage() {
//        return mLastPage;
//    }
//
//    public final Page<D> getLoadingPage() {
//        return mLoadingPage;
//    }
//
//    public final boolean isLoading(){
//        return null!=getLoadingPage();
//    }
//
//    public final D getLoadingPageArg(){
//        Page<D> data=mLoadingPage;
//        return null!=data?data.mArg:null;
//    }
//
//    @Override
//    public final boolean onLoadMore(RecyclerView rv, int state, String debug) {
//        if (isAllLoaded()){
////            Context context=null!=rv?rv.getContext():null;
////            if (null!=context){
////                Toast.makeText(context, R.string.noMoreData,Toast.LENGTH_SHORT).show();
////            }
//            return false;
//        }
//        RecyclerView.LayoutManager manager=null!=rv?rv.getLayoutManager():null;
//        if (null!=manager){
//            if (manager instanceof LinearLayoutManager){
//                LinearLayoutManager lm=(LinearLayoutManager)manager;
//                int firstPosition=lm.findFirstVisibleItemPosition();
//                View view=lm.findViewByPosition(firstPosition);
//                int[] location = new int[2];
//                int orientation=lm.getOrientation();
//                if (orientation==LinearLayoutManager.VERTICAL){
//                    rv.getLocationInWindow(location);
//                    int rvTop=location[1];
//                    if (null!=view) {
//                        view.getLocationInWindow(location);
//                        int viewTop = location[1] - view.getTop();
//                        if (viewTop == rvTop){
//                            return loadNextPage("After page not full " + (null != debug ? debug : "."));
//                        }
//                    }
//                    return resetAdapter("After page pull down.");
//                }else if (orientation==LinearLayoutManager.HORIZONTAL){
//                    rv.getLocationInWindow(location);
//                    int rvLeft=location[0];
//                    if (null!=view) {
//                        view.getLocationInWindow(location);
//                        int viewLeft = location[0] - view.getLeft();
//                        return viewLeft == rvLeft ? loadNextPage("After page not full " +
//                                (null != debug ? debug : ".")) : resetAdapter("After page pull left.");
//                    }
//                }
//            }else if (manager instanceof StaggeredGridLayoutManager){
////                ((StaggeredGridLayoutManager)manager).f
//            }
//        }
//        return false;
//    }
//
//    public final boolean isAllLoaded(){
//        Page<D> page=mCurrentPage;
//        Long total=null!=page?page.mTotal:null;
//        return null!=total&&getDataCount()>=total;
//    }
//
//    public final boolean loadNextPage(String debug){
//        Page<D> current=mCurrentPage;
//        int size=getDataCount();
//        return !isLoading()&&null!=current&&loadPage(new Page<>(current.mArg,size,null),false,debug);
//    }
//
//    public final boolean loadPage(D arg,String debug){
//        Page<D> current=mCurrentPage;
//        D currArg=null!=current?current.mArg:null;
//        if ((null==arg&&null==currArg)||(null!=currArg&&null!=arg&&currArg.equals(arg))){
//            return loadPage(new Page<>(arg,(null==current?0:current.mFrom),null),false,debug);
//        }
//        return loadPage(new Page<>(arg,0,null),true,debug);
//    }
//
//    protected void onNoMoreData(PageData<T> data){
//    }
//
//    private boolean loadPage(Page<D> page,boolean reset,String debug){
//        if (null!=page){
//            final int from=page.mFrom;
//            Page<D> loading=mLoadingPage;
//            Canceler canceler=null;
//            if (null!=loading){
//                if (isPageEquals(loading,page)) {
//                    Debug.W("Not need load page while exist loading." + loading);
//                    return false;
//                }
//                D args=loading.mArg;
//                D pageArgs=page.mArg;
//                if (null!=(canceler=!(null==pageArgs&&null==args)&&(null==pageArgs||null==args||
//                        !pageArgs.equals(args))?loading.mCanceler:null)){
//                    canceler.cancel(true,"Before load new arg page "+args+" new="+pageArgs);
//                }
//            }
//            mLoadingPage=page;
//            notifyPageUpdate(OnPageLoadUpdate.UPDATE_PAGE_START,true,page);
//            Debug.D("Load page "+ page.mArg+" from "+from+" "+(null!=debug?debug:"."));
//            if(null==(canceler=onPageLoad(page.mArg,from,(what, note, data, arg)->{
//                boolean idle=isPageEquals(mLoadingPage,page);
//                notifyPageUpdate(OnPageLoadUpdate.UPDATE_PAGE_END,idle,page);
//                if (idle){
//                    page.mCanceler=null;
//                    mLoadingPage=null;
//                    switch (what){
////                        case What.WHAT_OUT_OF_BOUNDS://Get through
////                            onNoMoreData(data);
////                        case What.WHAT_SUCCEED:
////                            long total=null!=data?data.getLength():-1;
////                            if (total>=0){
////                                mCurrentPage=new Page<>(page.mArg,page.mFrom,total);
////                                fillPage(data,reset);
////                            }
////                            onPageLoadSucceed(page.mArg,data,debug);
////                            break;
//                        default:
//                            Debug.D("Fail load page "+page.mArg+" from "+from+" "+(null!=debug?debug:".")+" "+this);
//                            break;
//                    }
//                } }))&&isPageEquals(mLoadingPage,page)){
//                Debug.W("Fail load page "+page.mArg+" from "+from+" "+(null!=debug?debug:".")+" "+this);
//                page.mCanceler=null;
//                mLoadingPage=null;
//                return false;
//            }
//            page.mCanceler=canceler;
//            return true;
//        }
//        Debug.W("Can't load page data.page="+page);
//        return false;
//    }
//
//    protected void onPageLoadSucceed(D arg,PageData<T> page,String debug){
//        //Do nothing
//    }
//
//    private boolean isPageEquals(Page<D> page1,Page<D>  page2){
//        return (null==page1&&null==page2)||(null!=page1&&null!=page2&&page1.equals(page2));
//    }
//
//    public final static class Page<T> {
//        private final int mFrom;
//        private final Long mTotal;
//        private final T mArg;
//        private Canceler mCanceler;
//
//        private Page(T arg,int from, Long total){
//            mArg=arg;
//            mFrom=from;
//            mTotal=total;
//        }
//
//        @Override
//        public boolean equals(@Nullable Object obj) {
//            if (null!=obj&&obj instanceof Page){
//                Page page=(Page)obj;
//                return mFrom==page.mFrom&&((null==mArg&&null==page.mArg)||(
//                        mArg.equals(page.mArg)));
//            }
//            return super.equals(obj);
//        }
//
//        @NonNull
//        @Override
//        public String toString() {
//            return ""+mArg+" "+mFrom+" "+super.toString();
//        }
//    }
//
//    public final boolean add(OnPageLoadUpdate update){
//        if (null!=update){
//            WeakHashMap<OnPageLoadUpdate,Object> reference=mUpdateListeners;
//            reference=null!=reference?reference:(mUpdateListeners=new WeakHashMap<>(1));
//            synchronized (reference){
//                if (!reference.containsKey(update)){
//                    reference.put(update,System.currentTimeMillis());
//                    return true;
//                }
//                return false;
//            }
//        }
//        return false;
//    }
//
//    private void notifyPageUpdate(int state,boolean idle,Page page){
//        WeakHashMap<OnPageLoadUpdate,Object> reference=mUpdateListeners;
//        Set<OnPageLoadUpdate> set=null!=reference?reference.keySet():null;
//        if (null!=set){
//            synchronized (set){
//                for (OnPageLoadUpdate update:set) {
//                    if (null!=update){
//                        update.onPageLoadUpdate(state,idle,page);
//                    }
//                }
//            }
//        }
//    }


}
