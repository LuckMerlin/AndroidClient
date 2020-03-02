package com.merlin.model;

import com.merlin.adapter.TransportAdapter;
import com.merlin.transport.TransportBinder;
import com.merlin.transport.Transporter;
import com.merlin.transport.Uploader;

public final class TransportModel extends Model implements Uploader.OnUploadProgress {
    private final TransportAdapter mAdapter=new TransportAdapter();
    private TransportBinder mBinder;

    @Override
    public void onUploadProgress(String from, String folder, String name, long upload, long total) {

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
