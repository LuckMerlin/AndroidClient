package com.merlin.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemConveyorBinding;
import com.merlin.conveyor._Convey;
import com.merlin.conveyor._ConveyGroup;
import com.merlin.transport.Status;

import java.util.List;

public class ConveyorAdapter<T extends _Convey> extends ListAdapter<T> implements OnItemTouchResolver, Status {

    private Integer formatStatus(_Convey convey){
        if (null!=convey){
            switch (convey.getStatus()){
                case PROGRESS:
                    return R.string.conveying;
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
                    Reply reply=convey instanceof _ConveyGroup ?((_ConveyGroup)convey).getFirstUnSucceedChildReply():convey.getReply();
                    Integer replyText=null!=convey?getReplyText(reply):null;
                    return null==replyText?R.string.finished:replyText;
            }
        }
        return null;
    }

    private Integer getReplyText(Reply reply){
        if (null!=reply){
            switch (reply.getWhat()){
                case What.WHAT_EXCEPTION:
                    return R.string.exception;
                case What.WHAT_TIMEOUT:
                    return R.string.timeout;
                case What.WHAT_NONE_NETWORK:
                    return R.string.networkException;
                case What.WHAT_CANCEL:
                    return R.string.canceled;
                default:
                    return reply.isSuccess()?R.string.finished:R.string.error;
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
            String status=null;
            String title=null;
            _Convey conveying=null;
            if (null!=data){
                Integer textId=formatStatus(data);
                if (null!=textId){
                    View view=null!=holder?holder.itemView:null;
                    Context context=null!=view?view.getContext():null;
                    status=null!=context?context.getString(textId):null;
                }
                itb.setData(data);
                if (data instanceof _ConveyGroup){
                    title=""+data.getName()+"("+(((_ConveyGroup) data).getRepliedChildrenSize())+"/"
                            +((_ConveyGroup) data).getChildCount()+")";
                    conveying=((_ConveyGroup)data).getConveying();
                }else{
                    conveying=data;
                    title=data.getName();
                }
            }
            itb.setConveyedSize(null!=conveying?conveying.getConveyed():0);
            itb.setTotalSize(null!=conveying?conveying.getTotal():0);
            itb.setConveyingSpeed(null!=conveying?conveying.getSpeed():-1);
            itb.setConveyingName(null!=conveying?conveying.getName():null);
            itb.setConveyingProgress(null!=conveying?(int)(conveying.getProgress()*100):0);
            itb.setTitle(title);
            itb.setStatus(null!=status&&status.length()>0?status:"");
            itb.setPosition(position+1);
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
