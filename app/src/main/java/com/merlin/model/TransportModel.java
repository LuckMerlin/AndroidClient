package com.merlin.model;

import android.content.res.Resources;

import com.merlin.adapter.TransportAdapter;
import com.merlin.api.Address;
import com.merlin.bean.ClientMeta;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.transport.Download;
import com.merlin.transport.OnStatusChange;
import com.merlin.transport.Transport;
import com.merlin.transport.TransportBinder;
import com.merlin.transport.Transporter;
import com.merlin.transport.Upload;

public final class TransportModel extends Model implements OnStatusChange {
    private final TransportAdapter mAdapter=new TransportAdapter();
    private TransportBinder mBinder;

    @Override
    public void onStatusChanged(int status, Transport transport) {
        TransportAdapter adapter=null!=transport?mAdapter:null;
        if (null!=adapter){
            switch (status){
                case TRANSPORT_ADD:
                    adapter.append(true,transport);break;
                case TRANSPORT_PAUSE:
                    adapter.updateErrorTextId(transport,getText(R.string.pause),"After pause status.");break;
                case TRANSPORT_TARGET_EXIST:
                    adapter.updateErrorTextId(transport,getText(R.string.fileAlreadyExist),"After exist status.");break;
                case TRANSPORT_FAIL:
                    adapter.updateErrorTextId(transport,getText(R.string.fail),"After fail status.");break;
                case TRANSPORT_START:
                    adapter.updateErrorTextId(transport,getText(Resources.ID_NULL),"After start status.");break;
                case TRANSPORT_PROGRESS:
                    adapter.update("After status change.",transport);break;
                case TRANSPORT_SKIP:
                    adapter.remove(transport, getText(R.string.skip),"After status change."+status);break;
                case TRANSPORT_CANCEL:
                    adapter.remove(transport, getText(R.string.cancel),"After status change."+status);break;
                case TRANSPORT_ERROR:
                    adapter.remove(transport, getText(R.string.error),"After status change."+status);break;
                case TRANSPORT_REMOVE:
                    adapter.remove(transport, getText(R.string.remove),"After status change."+status);break;
                case TRANSPORT_SUCCEED:
                    adapter.remove(transport, getText(R.string.succeed),"After status change."+status);
                    if (status==TRANSPORT_SUCCEED){
                        post(()->{
                            if (transport instanceof Upload){
                                testUpload();
                            }else{
                                testDownload();
                            }
                        },5000);
                    }
                    break;
            }
        }
    }


    private void testDownload(){
        TransportBinder binder=mBinder;
        if (null!=binder) {
            ClientMeta client = new ClientMeta("林强设备", Address.URL, "", "");
            Transport transport = new Download("../林强.mp4", "/sdcard/a",
//                Transport transport=new Download("./test2.mp3","/sdcard/a",
                    "林强.mp4", client, null);
            binder.run(TRANSPORT_ADD, transport, "Test.");
        }
    }

    private void testUpload(){
        TransportBinder binder=mBinder;
        if (null!=binder) {
                ClientMeta client=new ClientMeta("林强设备", Address.URL,"","");
                Transport transport=new Upload("/sdcard/Musics/大壮 - 我们不一样.mp3","./data",
                        "林强.mp3",client,null);
                binder.run(TRANSPORT_ADD,transport,"Test.");
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
                testDownload();
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
