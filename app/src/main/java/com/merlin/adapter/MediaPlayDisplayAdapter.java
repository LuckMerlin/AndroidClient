package com.merlin.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.binding.ModelBinder;
import com.merlin.client.R;
import com.merlin.model.MediaDisplayModel;
import com.merlin.model.Model;
import com.merlin.player.Media;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MediaPlayDisplayAdapter extends ListAdapter<Integer> implements OnRecyclerScrollStateChange{
    private final PagerSnapHelper mHelper=new PagerSnapHelper();
    private LinearLayoutManager mManager;
    private OnRecyclerScrollStateChange mChange;
    private WeakReference<Media> mPlaying;

    public interface OnMediaPlayModelShow{
        void onMediaPlayModelShow();
    }

    public MediaPlayDisplayAdapter(){
        this(null);
    }

    public MediaPlayDisplayAdapter(OnRecyclerScrollStateChange change){
        List<Integer> list=new ArrayList<>(3);
        list.add(R.layout.media_display_sheet_category);
        list.add(R.layout.media_display_play);
        list.add(R.layout.media_display_all_medias);
        set(list,"");
        mChange=change;
    }

    public final View getCurrentView(){
        LinearLayoutManager manager=mManager;
        PagerSnapHelper helper=mHelper;
        return null!=manager&&null!=helper?helper.findSnapView(manager):null;
    }

    public boolean showDisplay(Class<? extends ViewDataBinding> cls){
        if (null!=cls){
            LinearLayoutManager manager=mManager;
            PagerSnapHelper helper=mHelper;
            View view=null!=helper?helper.findSnapView(manager):null;
            ViewParent parent=null!=view?view.getParent():null;
            RecyclerView recyclerView=null!=parent&&parent instanceof RecyclerView?((RecyclerView)parent):null;
            if (null!=recyclerView){
                int count=recyclerView.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child=recyclerView.getChildAt(i);
                    ViewDataBinding binding=null!=child?DataBindingUtil.getBinding(child):null;
                    if (null!=binding&&binding.getClass().equals(cls)){
                        recyclerView.smoothScrollToPosition(i);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public final Model getCurrentModel(){
        View root=getCurrentView();
        Object object=null!=root?root.getTag(R.id.modelBind):null;
        return null!=object&&object instanceof Model?((Model)object):null;
    }

    protected final Media getPlaying() {
        WeakReference<Media> reference=mPlaying;
        return null!=reference?reference.get():null;
    }

    public final boolean setPlaying(Media playable){
        WeakReference<Media> playing=mPlaying;
        mPlaying=null;
        if (null!=playing){
            playing.clear();
        }
        if (null!=playable){
            mPlaying=new WeakReference<>(playable);
        }
        setCurrentPlaying(playable);
        return true;
    }

    private void setCurrentPlaying(Media playing){
        View root=getCurrentView();
        if (null!=root){
            applyPlaying(root,playing);
        }
    }

    private void applyPlaying(View root, Media playing){
        if (playing==null){
            WeakReference<Media> reference=null!=root?mPlaying:null;
            playing=null!=reference?reference.get():null;
        }
        Model model=null!=playing?ModelBinder.getBindModel(root):null;
        if (null!=model&&model instanceof MediaDisplayModel){
            ((MediaDisplayModel)model).onPlayingChange(playing);
        }
    }

    @Override
    public void onAttachedRecyclerView(RecyclerView recyclerView) {
        super.onAttachedRecyclerView(recyclerView);
        recyclerView.scrollToPosition(1);
    }

        @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(LayoutInflater in, int viewType, ViewGroup parent) {
        if (viewType!= Resources.ID_NULL&&viewType!=-1){
            ViewDataBinding binding= DataBindingUtil.inflate(in,viewType, parent, false);
            View  root=null!=binding?binding.getRoot():null;
            RecyclerView.ViewHolder viewHolder=null!=root?new ViewHolder(binding):new BaseViewHolder(new View(parent.getContext()));
            root.postDelayed(()->applyPlaying(root,null),500);
            return viewHolder;
        }
        return null;
    }

    @Override
    protected int getItemViewType(int position, int size) {
        Integer data=getItemData(position);
        return null!=data?data:-1;
    }


    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        if (null!=rv){
            mManager=new LinearLayoutManager(rv.getContext(),LinearLayoutManager.HORIZONTAL,false);
            mHelper.attachToRecyclerView(rv);
            return mManager;
        }
        return null;
    }

    @Override
    public void onRecyclerScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState==RecyclerView.SCROLL_STATE_IDLE){
            Model model=getCurrentModel();
            if (null!=model&&model instanceof OnMediaPlayModelShow){
                ((OnMediaPlayModelShow)model).onMediaPlayModelShow();
            }
        }
        if (newState==RecyclerView.SCROLL_STATE_IDLE){
            setCurrentPlaying(getPlaying());
        }
        OnRecyclerScrollStateChange change=mChange;
        if (null!=change){
            change.onRecyclerScrollStateChanged(recyclerView,newState);
        }

    }
}
