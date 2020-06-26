package com.merlin.binding;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.merlin.adapter.ListAdapter;
import com.merlin.adapter.LoadMoreInterceptor;
import com.merlin.adapter.OnItemTouchResolver;
import com.merlin.adapter.OnMoreLoadable;
import com.merlin.adapter.OnRecyclerScroll;
import com.merlin.adapter.OnRecyclerScrollStateChange;
import com.merlin.adapter.PageAdapter;
import com.merlin.adapter.PageAdapterRefreshBridge;
import com.merlin.adapter.RecycleViewScrollQuantize;
import com.merlin.api.Res;
import com.merlin.click.Clicker;
import com.merlin.model.ModelBinder;

import java.util.Collection;

import static com.merlin.adapter.PageAdapter.OnPageLoadUpdate.UPDATE_PAGE_START;

public class LMBinding {

    @BindingAdapter("text")
    public static void setText(TextView view, Object resId) {
        if (null!=view) {
            view.setText("");
            resId = null == resId ? "" : resId;
            if (resId instanceof String) {
                view.setText((String)resId);
            } else if (resId instanceof Integer&&(Integer)resId!= Resources.ID_NULL) {
                view.setText((Integer)resId);
                Clicker.putRes(view,new Res((Integer)resId,null));
            }
        }
    }

    @BindingAdapter("layout")
    public static void inflateLayout(View view, Object layout) {
        if (null!=view&&view instanceof ViewGroup &&null!=layout){
            if (layout instanceof Integer){
                if (!layout.equals(Resources.ID_NULL)){
                    DataBindingUtil.inflate(LayoutInflater.from(view.getContext()),(Integer)layout,(ViewGroup)view,true);
                }
            }else if (layout instanceof View&&null==((View)layout).getParent()){
                ViewGroup.LayoutParams params=view.getLayoutParams();
                int width=null!=params?params.width:ViewGroup.LayoutParams.WRAP_CONTENT;
                int height=null!=params?params.height:ViewGroup.LayoutParams.WRAP_CONTENT;
                ((ViewGroup)view).addView((View)layout,new ViewGroup.LayoutParams(width,height));
            }else if (layout instanceof ViewDataBinding){
                View root=((ViewDataBinding)layout).getRoot();
                if (null!=root&&null==root.getParent()){
                    inflateLayout(view,root);
                }
            }else if (layout instanceof Collection &&((Collection)layout).size()>0){
                for (Object child:(Collection)layout) {
                    inflateLayout(view,child);
                }
            }
        }
    }

    @BindingAdapter(value = {"enableClick"})
    public static void enableClick(View view, Object obj) {
        if (null!=view&&null!=obj){
            if (obj instanceof Boolean){
                new Clicker().attach(view,(Boolean)obj);
            }else if (obj instanceof Clicker.Click){
                new Clicker().attach(view,(Clicker.Click)obj);
            }
        }
    }

    @BindingAdapter(value = {"createModel"})
    public static void createModel(View view, Object modeClass) {
        new ModelBinder().bind(view,modeClass);
    }

     @BindingAdapter("adapter")
     public static void adapter(RecyclerView view, ListAdapter adapter) {
         if (null!=view){
             if (null!=adapter&&adapter instanceof ListAdapter){
                 Object helper=adapter instanceof OnItemTouchResolver?((OnItemTouchResolver)adapter).onResolveItemTouch(view):null;
                 helper=null!=helper&&helper instanceof ItemTouchHelper.Callback?new ItemTouchHelper((ItemTouchHelper.Callback)helper):helper;
                 if (null!=helper&&helper instanceof ItemTouchHelper){
                     ((ItemTouchHelper)helper).attachToRecyclerView(view);
                 }
                 RecyclerView.LayoutManager manager=((ListAdapter)adapter).onResolveLayoutManager(view);
                 if (null!=manager){
                     view.setLayoutManager(manager);
                 }
                 if (adapter instanceof OnMoreLoadable || adapter instanceof OnRecyclerScroll||adapter instanceof OnRecyclerScrollStateChange){
                     view.addOnScrollListener(new RecycleViewScrollQuantize());
                     view.addOnScrollListener(new LoadMoreInterceptor(){
                         @Override
                         protected void onLoadMore(RecyclerView recyclerView,int state, String debug) {
                             if (adapter instanceof OnMoreLoadable) {
                                 ((OnMoreLoadable) adapter).onLoadMore(recyclerView, state, debug);
                             }
                         }

                         @Override
                         protected void onRecyclerScrolled(RecyclerView recyclerView, int dx, int dy) {
                             if (adapter instanceof OnRecyclerScroll){
                                 ((OnRecyclerScroll)adapter).onRecyclerScrolled(recyclerView,dx,dy);
                             }
                         }

                         @Override
                         protected void onRecyclerScrollStateChanged(RecyclerView recyclerView, int newState) {
                             if (adapter instanceof OnRecyclerScrollStateChange){
                                 ((OnRecyclerScrollStateChange)adapter).onRecyclerScrollStateChanged(recyclerView,newState);
                             }
                         }
                     });
                 }
                 final ViewParent parent=adapter instanceof PageAdapter ?view.getParent():null;
                 if (null!=parent&&parent instanceof SwipeRefreshLayout){
                     final SwipeRefreshLayout refreshLayout=(SwipeRefreshLayout)parent;
                     final PageAdapter pageAdapter=(PageAdapter)adapter;
                     refreshLayout.setRefreshing(pageAdapter.isLoading());
                     final PageAdapterRefreshBridge refresh=new PageAdapterRefreshBridge(){
                         @Override
                         public void onPageLoadUpdate(int state, boolean idle, PageAdapter.Page page) {
                             switch (state){
                                 case UPDATE_PAGE_START:
                                     refreshLayout.setRefreshing(true);
                                     break;
                                 case UPDATE_PAGE_END:
                                     if (idle){
                                         refreshLayout.setRefreshing(false);
                                     }
                                     break;
                             }
                         }

                         @Override
                         public void onRefresh() {
                             if (!pageAdapter.resetAdapter("After refresh.")&&!pageAdapter.isLoading()){
                                 refreshLayout.setRefreshing(false);
                             }
                         }
                     };
                     ((SwipeRefreshLayout)parent).setOnRefreshListener(refresh);
                     pageAdapter.add(refresh);
                 }
             }
             view.setAdapter(adapter);
         }
     }

}
