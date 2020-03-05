package com.merlin.model;

import com.merlin.adapter.TransportAdapter;
import com.merlin.api.Address;
import com.merlin.bean.ClientMeta;
import com.merlin.debug.Debug;
import com.merlin.transport.Download;
import com.merlin.transport.OnStatusChange;
import com.merlin.transport.Transport;
import com.merlin.transport.TransportBinder;
import com.merlin.transport.Transporter;

public final class TransportModel extends Model implements OnStatusChange {
    private final TransportAdapter mAdapter=new TransportAdapter();
    private TransportBinder mBinder;

    @Override
    public void onStatusChanged(int status, Transport transport) {
        TransportAdapter adapter=null!=transport?mAdapter:null;
        if (null!=adapter){
            switch (status){
                case TRANSPORT_ADD:
                    adapter.append(true,transport);
                    break;
                case TRANSPORT_REMOVE:
                    adapter.remove(transport,"After status remove.");
                    break;
                case TRANSPORT_PAUSE:// Get through
                case TRANSPORT_TARGET_EXIST:// Get through
                case TRANSPORT_FAIL:// Get through
                case TRANSPORT_START:// Get through
                    transport.setStatus(status);// Get through
                case TRANSPORT_PROGRESS:// Get through
                    adapter.update("After status change.",transport);
                    break;
                case TRANSPORT_SKIP:// Get through
                case TRANSPORT_CANCEL:// Get through
                case TRANSPORT_ERROR:
                    adapter.remove(transport,"After status change."+status);
                    break;
            }
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
//                ClientMeta client=new ClientMeta("林强设备", Address.URL,"","");
//                Transport transport=new Upload("/sdcard/Musics/大壮 - 我们不一样.mp3","./data",
//                        "林强.mp3",client,null);
//                binder.run(TRANSPORT_ADD,transport,"Test.");
                //
                ClientMeta client=new ClientMeta("林强设备", Address.URL,"","");
//                Transport transport=new Download("./data/林强.mp3","/sdcard/a",
                Transport transport=new Download("./test2.mp3","/sdcard/a",
                        "林强.mp3",client,null);
                binder.run(TRANSPORT_ADD,transport,"Test.");
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
