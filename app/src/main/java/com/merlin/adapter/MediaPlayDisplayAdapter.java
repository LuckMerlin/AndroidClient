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
import com.merlin.model.Model;

public class MediaPlayDisplayAdapter extends Adapter<Integer> implements OnRecyclerScrollStateChange{
    private final PagerSnapHelper mHelper=new PagerSnapHelper();
    private LinearLayoutManager mManager;
    private OnRecyclerScrollStateChange mChange;

    public MediaPlayDisplayAdapter(OnRecyclerScrollStateChange change){
        super(R.layout.media_display_sheet_category);
//        super(R.layout.media_display_play,R.layout.media_display_all_medias,R.layout.media_display_sheet_category);
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

    @NonNull
    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        final LayoutInflater in=LayoutInflater.from(context);
        ViewDataBinding binding= DataBindingUtil.inflate(in,viewType, parent, false);
        ViewHolder viewHolder=new ViewHolder(null!=binding?binding.getRoot():null);
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
        OnRecyclerScrollStateChange change=mChange;
        if (null!=change){
            change.onRecyclerScrollStateChanged(recyclerView,newState);
        }
    }
}
