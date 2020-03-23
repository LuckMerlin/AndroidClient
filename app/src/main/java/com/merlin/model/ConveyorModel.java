package com.merlin.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.adapter.ConveyorAdapter;
import com.merlin.transport.Convey;
import com.merlin.transport.ConveyorBinder;
import com.merlin.transport.OnConveyStatusChange;

public final class ConveyorModel extends Model implements OnConveyStatusChange {
    private ConveyorBinder mBinder;

    private final ConveyorAdapter mAdapter=new ConveyorAdapter(){
        @Override
        public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder, View view, ViewDataBinding binding) {
//            AbsTransport transport=null!=binding&&binding instanceof ItemTransportBinding ?((ItemTransportBinding)binding).getData():null;
//            TransportBinder binder=mBinder;
//            if (null!=binder){
//                binder.run(Callback.TRANSPORT_CANCEL,false,"After remove from view.",transport);
//            }
        }
    };

    @Override
    public void onConveyStatusChanged(int status, Convey convey, Object data) {

    }

    //    @Override
//    public void onStatusChanged(int status, AbsTransport transport, Object data){
//        TransportAdapter adapter=null!=transport?mAdapter:null;
//        if (null!=adapter){
//            switch (status){
//                case TRANSPORT_ADD:adapter.append(true,transport);break;
//                case TRANSPORT_PAUSE:adapter.updateErrorTextId(transport,getText(R.string.pause),"After pause status.");break;
//                case TRANSPORT_TARGET_EXIST:adapter.updateErrorTextId(transport,getText(R.string.fileAlreadyExist),"After exist status.");break;
//                case TRANSPORT_FAIL:adapter.updateErrorTextId(transport,getText(R.string.fail),"After fail status.");break;
//                case TRANSPORT_START:adapter.updateErrorTextId(transport,getText(Resources.ID_NULL),"After start status.");break;
//                case TRANSPORT_QUEUING:adapter.updateErrorTextId(transport,getText(R.string.queuing),"After start status.");break;
//                case TRANSPORT_PROGRESS:adapter.update("After status change.",transport);break;
//                case TRANSPORT_SKIP:
//                case TRANSPORT_CANCEL:
//                case TRANSPORT_ERROR:
//                    remove(transport,500,"After status change."+status);break;
////                case TRANSPORT_REMOVE:
////                    remove(transport, 50,"After status change."+status);break;
//                case TRANSPORT_PREPARE_BLOCK:
//                    if (null!=data&&data instanceof Block){showTransportBlock((Block)data);}break;
//                case TRANSPORT_SUCCEED:
//                    remove(transport,300,"After status change."+status); break;
//            }
//        }
//    }

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
                mAdapter.setData(binder.get(null));
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
