package com.merlin.model;

import android.content.res.Resources;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.adapter.TransportAdapter;
import com.merlin.api.Address;
import com.merlin.bean.ClientMeta;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemTransportBinding;
import com.merlin.transport.Block;
import com.merlin.transport.Callback;
import com.merlin.transport.Download;
import com.merlin.transport.OnStatusChange;
import com.merlin.transport.AbsTransport;
import com.merlin.transport.Transport;
import com.merlin.transport.TransportBinder;
import com.merlin.transport.Transporter;
import com.merlin.transport.Upload;

public final class TransportModel extends Model implements OnStatusChange {
    private TransportBinder mBinder;
    private final TransportAdapter mAdapter=new TransportAdapter(){
        @Override
        public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder, View view, ViewDataBinding binding) {
            AbsTransport transport=null!=binding&&binding instanceof ItemTransportBinding ?((ItemTransportBinding)binding).getData():null;
            TransportBinder binder=mBinder;
            if (null!=binder){
                binder.run(Callback.TRANSPORT_CANCEL,false,"After remove from view.",transport);
            }
        }
    };

    @Override
    public void onStatusChanged(int status, AbsTransport transport, Object data){
        TransportAdapter adapter=null!=transport?mAdapter:null;
        if (null!=adapter){
            switch (status){
                case TRANSPORT_ADD:adapter.append(true,transport);break;
                case TRANSPORT_PAUSE:adapter.updateErrorTextId(transport,getText(R.string.pause),"After pause status.");break;
                case TRANSPORT_TARGET_EXIST:adapter.updateErrorTextId(transport,getText(R.string.fileAlreadyExist),"After exist status.");break;
                case TRANSPORT_FAIL:adapter.updateErrorTextId(transport,getText(R.string.fail),"After fail status.");break;
                case TRANSPORT_START:adapter.updateErrorTextId(transport,getText(Resources.ID_NULL),"After start status.");break;
                case TRANSPORT_QUEUING:adapter.updateErrorTextId(transport,getText(R.string.queuing),"After start status.");break;
                case TRANSPORT_PROGRESS:adapter.update("After status change.",transport);break;
                case TRANSPORT_SKIP:
                case TRANSPORT_CANCEL:
                case TRANSPORT_ERROR:
                    remove(transport,500,"After status change."+status);break;
//                case TRANSPORT_REMOVE:
//                    remove(transport, 50,"After status change."+status);break;
                case TRANSPORT_PREPARE_BLOCK:
                    if (null!=data&&data instanceof Block){showTransportBlock((Block)data);}break;
                case TRANSPORT_SUCCEED:
                    remove(transport,300,"After status change."+status); break;
            }
        }
    }

    private boolean remove(Transport transport,int delay,String debug){
        TransportAdapter adapter=null!=transport?mAdapter:null;
        if (null!=adapter){
            return post(()->adapter.remove(transport,debug),delay<=0?0:delay);
        }
        return false;
    }

    private boolean showTransportBlock(Block block){
        if (null!=block) {
//            final Dialog dialog = new Dialog(getViewContext());
//            return dialog.create().title(R.string.conflict).message("毒蛇啦").show();
        }
        return false;
    }

    private void testDownload(){
        TransportBinder binder=mBinder;
        if (null!=binder) {
            ClientMeta client = new ClientMeta("林强设备", Address.URL, "", "","","\\");
            AbsTransport transport = new Download("../林强.mp4", "/sdcard/a",
//                Transport transport=new Download("./test2.mp3","/sdcard/a",
                    "林强.mp4", client, null);
            binder.run(TRANSPORT_ADD, true,"Test.",transport);
        }
    }

    private void testUpload(){
        TransportBinder binder=mBinder;
        if (null!=binder) {
                ClientMeta client=new ClientMeta("林强设备", Address.URL,"","",".","/");
//                Transport transport=new Upload("/sdcard/Musics/大壮 - 我们不一样.mp3","./data",
                AbsTransport transport=new Upload("/sdcard/Musics/大壮 - 我们不一样.mp3",null,
                        "林强.mp3",client,null);
                binder.run(TRANSPORT_ADD,true,"Test.",transport);
        }
    }

    public boolean setBinder(TransportBinder binder, String debug){
        TransportBinder current=mBinder;
        if (null!=binder){
            if (null==current||current!=binder){
                mBinder=binder;
                binder.callback(TRANSPORT_ADD,this);
                mAdapter.setData(binder.getRunning(Transporter.TYPE_ALL));
                //test begin
                testUpload();
//                testDownload();
                //test end
                return true;
            }
        }else if (null!=current){
            current.callback(TRANSPORT_REMOVE,this);
            mBinder=null;
            return true;
        }
        return false;
    }

    public TransportAdapter getAdapter() {
        return mAdapter;
    }

}
