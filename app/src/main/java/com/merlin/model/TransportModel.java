package com.merlin.model;

import android.view.View;

import com.merlin.adapter.TransportAdapter;
import com.merlin.transport.Transport;
import com.merlin.transport.TransportBinder;
import com.merlin.transport.Transporter;
import com.merlin.transport.Uploader;

public final class TransportModel extends Model implements Uploader.OnStatusChange {
    private final TransportAdapter mAdapter=new TransportAdapter();
    private TransportBinder mBinder;

    @Override
    protected void onRootAttached(View root) {
        super.onRootAttached(root);
//        List<Transport> list=new ArrayList<>();
//        Upload upload=new Upload("path","/dddd","林强",null);
//        list.add(upload);
//        upload=new Upload("path","/dddd","林强",null);
//        list.add(upload);
        //
//        mAdapter.setData(list);
    }

    @Override
    public void onStatusChanged(int status, Transport transport, long upload, long total) {
        TransportAdapter adapter=null!=transport?mAdapter:null;
        if (null!=adapter){
            switch (status){
                case TRANSPORT_STATUS_ADD:
                    adapter.append(true,transport);
                    break;
                case TRANSPORT_STATUS_REMOVE:
                    adapter.remove(transport,"After status remove.");
                    break;
                case TRANSPORT_STATUS_PAUSE:// Get through
                case TRANSPORT_STATUS_PROGRESS:// Get through
                case TRANSPORT_STATUS_START:
                    adapter.update("After status change.",transport);
                    break;
            }
        }
    }

    public boolean setBinder(TransportBinder binder, String debug){
        TransportBinder current=mBinder;
        if (null!=binder){
            if (null==current||current!=binder){
                mBinder=binder;
                binder.add(this);
                mAdapter.setData(binder.getRunning(Transporter.TYPE_ALL));
                return true;
            }
        }else if (null!=current){
            current.remove(this);
            mBinder=null;
            return true;
        }
        return false;
    }

    public TransportAdapter getAdapter() {
        return mAdapter;
    }
}
