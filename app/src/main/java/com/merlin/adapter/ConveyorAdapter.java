package com.merlin.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.client.R;
import com.merlin.client.databinding.ItemConveyorBinding;
import com.merlin.conveyor.StatusTextFormat;
import com.merlin.transport.Convey;
import com.merlin.transport.ConveyGroup;
import com.merlin.transport.Status;

import java.util.List;

public class ConveyorAdapter<T extends Convey> extends ListAdapter<T> implements OnItemTouchResolver, Status {
//    private final StatusTextFormat mFormat=new StatusTextFormat();


    private Integer formatStatus(Convey convey){
        if (null!=convey){
            switch (convey.getStatus()){
                case CANCELED:
                    return R.string.canceled;
                case PREPARING:
                    return R.string.preparing;
                case PREPARED:
                    return R.string.prepared;
                case STARTED:
                    return R.string.started;
                case PAUSED:
                    return R.string.paused;
                case FINISHED:
                    if (convey instanceof ConveyGroup){
                        Convey unSucceedReply= ((ConveyGroup)convey).getFirstUnSucceedReply();
//                        return unSucceedReply.getReply()
                    }
                    return R.string.finished;
            }
        }
        return null;
    }

    @Override
    protected Integer onResolveViewTypeLayoutId(int viewType) {
        return viewType==TYPE_DATA?R.layout.item_conveyor:null;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType, ViewDataBinding binding, int position, T data, @NonNull List<Object> payloads) {
        if (null!=binding&&binding instanceof ItemConveyorBinding){
            ItemConveyorBinding itb=(ItemConveyorBinding)binding;
            Integer textId=formatStatus(data);
            String status=null;
            if (null!=textId){
                View view=null!=holder?holder.itemView:null;
                Context context=null!=view?view.getContext():null;
                status=null!=context?context.getString(textId):null;
            }
            itb.setStatus(null!=status&&status.length()>0?status:"");
            itb.setPosition(position+1);
            itb.setData(data);
        }
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        return new LinearLayoutManager(rv.getContext(), RecyclerView.VERTICAL,false);
    }

    @Override
    public Object onResolveItemTouch(RecyclerView recyclerView) {
        return new ItemSlideRemover();
    }

}
