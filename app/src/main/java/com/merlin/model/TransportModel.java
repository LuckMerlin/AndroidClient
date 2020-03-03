package com.merlin.model;

import android.view.View;

import com.merlin.adapter.TransportAdapter;
import com.merlin.transport.Transport;
import com.merlin.transport.TransportBinder;
import com.merlin.transport.Transporter;
import com.merlin.transport.Upload;
import com.merlin.transport.Uploader;

import java.util.ArrayList;
import java.util.List;

public final class TransportModel extends Model implements Uploader.OnUploadProgress {
    private final TransportAdapter mAdapter=new TransportAdapter();
    private TransportBinder mBinder;

    @Override
    protected void onRootAttached(View root) {
        super.onRootAttached(root);
        List<Transport> list=new ArrayList<>();
        Upload upload=new Upload("path","/dddd","林强",null);
        list.add(upload);
        upload=new Upload("path","/dddd","林强",null);
        list.add(upload);
        mAdapter.setData(list);
    }

    @Override
    public void onUploadProgress(Transport transport, long upload, long total) {

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
