package com.merlin.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.adapter.ConveyingAdapter;
import com.merlin.adapter.ConveyorAdapter;
import com.merlin.client.R;
import com.merlin.client.databinding.ConveyDetailBinding;
import com.merlin.client.databinding.ItemConveyorBinding;
import com.merlin.conveyor.ConveyGroup;
import com.merlin.debug.Debug;
import com.merlin.dialog.Dialog;
import com.merlin.conveyor.Convey;
import com.merlin.conveyor.ConveyorBinder;
import com.merlin.transport.OnConveyStatusChange;
import com.merlin.view.OnTapClick;

import java.util.List;

public final class ConveyorModel extends Model implements OnConveyStatusChange, OnTapClick {
    private ConveyorBinder mBinder;

    private final ConveyorAdapter mAdapter=new ConveyorAdapter(){
        @Override
        public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder, View view, ViewDataBinding binding) {
            Convey convey=null!=binding&&binding instanceof ItemConveyorBinding ?((ItemConveyorBinding)binding).getData():null;
            ConveyorBinder binder=mBinder;
            if (null!=binder&&(null==convey||index(convey)<0)){
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
                    mAdapter.removeData(convey,"While finished with succeed.");
                }
            default:
                if (null!=convey) {
                    mAdapter.replace(convey, "While status changed." + status);
                }
                break;
        }
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (resId){
            default:
                if (null!=data&&data instanceof Convey){
                    showConvey((Convey)data,"After tap click.");
                }
                break;
        }
        return true;
    }

    public boolean showConvey(Convey convey,String debug){
        if (null!=convey){
            ConveyDetailBinding binding=inflate(R.layout.convey_detail);
            final OnConveyStatusChange change=(int status, Convey c, Object data)-> {
                binding.setConvey(null!=data&&data instanceof Convey?(Convey)data:c);
            };
            final Dialog dialog=new Dialog(getViewContext()){
                @Override
                protected void onDismiss() {super.onDismiss();
                    convey.setListener(null,"While dialog dismiss.");
                }
            };
            dialog.setContentView(binding);
            if (null!=binding){
                binding.setChildAdapter(convey instanceof ConveyGroup?new ConveyingAdapter(((ConveyGroup)convey).getChildren()) :null);
                binding.setConvey(convey);
                convey.setListener(change,"While show convey detail dialog.");
            }
            return dialog.title(convey.getName()).left(R.string.sure).show(
                    (View view, int clickCount, int resId, Object data)-> {
                        dialog.dismiss();
                    return false;
            });
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
                Debug.D(getClass(),"AAA setBinder AAAAAAAAA "+(null!=conveys?conveys.size():-1));
                mAdapter.set(conveys,"After conveyor bind.");
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
