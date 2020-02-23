package com.merlin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.media.Mode;
import com.merlin.model.MediaDisplayModel;
import com.merlin.model.Model;
import com.merlin.player.Playable;

import java.lang.ref.WeakReference;

public class MediaPlayDisplayAdapter extends Adapter<Integer> implements OnRecyclerScrollStateChange{
    private final PagerSnapHelper mHelper=new PagerSnapHelper();
    private LinearLayoutManager mManager;
    private OnRecyclerScrollStateChange mChange;
    private WeakReference<Playable> mPlaying;

    public interface OnMediaPlayModelShow{
        void onMediaPlayModelShow();
    }

    public MediaPlayDisplayAdapter(){
        this(null);
    }

    public MediaPlayDisplayAdapter(OnRecyclerScrollStateChange change){
        super(R.layout.media_display_sheet_category,R.layout.media_display_play,R.layout.media_display_all_medias);
        mChange=change;
    }

    public final View getCurrentView(){
        LinearLayoutManager manager=mManager;
        PagerSnapHelper helper=mHelper;
        return null!=manager&&null!=helper?helper.findSnapView(manager):null;
    }

    public final Model getCurrentModel(){
        View root=getCurrentView();
        Object object=null!=root?root.getTag(R.id.modelBind):null;
        return null!=object&&object instanceof Model?((Model)object):null;
    }

    protected final Playable getPlaying() {
        WeakReference<Playable> reference=mPlaying;
        return null!=reference?reference.get():null;
    }

    public final boolean setPlaying(Playable playable){
        WeakReference<Playable> playing=mPlaying;
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

    private void setCurrentPlaying(Playable playing){
        Model model=getCurrentModel();
        if (null!=model&&model instanceof MediaDisplayModel){
            ((MediaDisplayModel)model).onPlayingChange(playing);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.scrollToPosition(1);
    }

    @NonNull
    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        final LayoutInflater in=LayoutInflater.from(context);
        ViewDataBinding binding= DataBindingUtil.inflate(in,viewType, parent, false);
        ViewHolder viewHolder=new ViewHolder(null!=binding?binding.getRoot():new View(context));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemViewType(int position) {
        Integer data=getItem(position);
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
