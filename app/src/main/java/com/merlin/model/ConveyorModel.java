package com.merlin.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.adapter.ConveyorAdapter;
import com.merlin.client.databinding.ItemConveyorBinding;
import com.merlin.transport.Convey;
import com.merlin.transport.ConveyorBinder;
import com.merlin.transport.OnConveyStatusChange;

import java.util.List;

public final class ConveyorModel extends Model implements OnConveyStatusChange {
    private ConveyorBinder mBinder;

    private final ConveyorAdapter mAdapter=new ConveyorAdapter(){
        @Override
        public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder, View view, ViewDataBinding binding) {
            Convey convey=null!=binding&&binding instanceof ItemConveyorBinding ?((ItemConveyorBinding)binding).getData():null;
            ConveyorBinder binder=mBinder;
            if (null!=binder&&index(convey)<0){
                binder.run(CANCELED,"After remove from view.",convey);
            }
        }
    };

    @Override
    public void onConveyStatusChanged(int status, Convey convey, Object data) {
        switch (status){
            case ADD:
                break;
            case FINISHED:// Get through
                if (null!=convey&&convey.isSuccessFinished()){//Remove succeed
                    mAdapter.remove(convey,"While finished with succeed.");
                }
            default:
                if (null!=convey) {
                    mAdapter.replace(convey, "While status changed." + status);
                }
                break;
        }
    }

    private boolean remove(Convey convey, int delay, String debug){
        ConveyorAdapter adapter=null!=convey?mAdapter:null;
        if (null!=adapter){
            return post(()->adapter.remove(convey,debug),delay<=0?0:delay);
        }
        return false;
    }

    public boolean setBinder(ConveyorBinder binder, String debug){
        ConveyorBinder current=mBinder;
        if (null!=binder){
            if (null==current||current!=binder){
                mBinder=binder;
                binder.listener(ADD,this,debug);
                List<Convey> conveys=binder.get(null);
                mAdapter.setData(conveys);
                return true;
            }
        }else if (null!=current){
            current.listener(CANCELED,this,debug);
            mBinder=null;
            return true;
        }
        return false;
    }

    public ConveyorAdapter getAdapter() {
        return mAdapter;
    }

}
