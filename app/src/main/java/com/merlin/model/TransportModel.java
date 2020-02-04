package com.merlin.model;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.activity.TransportActivity;
import com.merlin.adapter.DownloadAdapter;
import com.merlin.client.R;
import com.merlin.task.Transport;
import com.merlin.task.DownloadService;
import com.merlin.task.Transporter;

import java.util.List;

public class TransportModel extends BaseModel implements BaseModel.OnModelViewClick, DownloadService.Callback {
    private Transporter mDownloader;
    private final DownloadAdapter mAdapter=new DownloadAdapter();

    public TransportModel(Context context){
        super(context);
        mAdapter.setViewClickListener(this);
    }

    public void setDownloader(Transporter downloader){
        Transporter curr=mDownloader;
        mDownloader=downloader;
        if (null!=downloader){
            downloader.setCallback(this);
            updateList();
//            String fromAccount,String src,String name,String targetFolder,String unique
//            Transport download=new Transport(null,"/volume1/");
//            downloader.download(download);
        }else if(null!=curr){
            curr.setCallback(null);
        }
    }

    private void updateList(){
        Transporter downloader=mDownloader;
        setList(null!=downloader?downloader.getDownloadList():null);
    }

    @Override
    public void onViewClick(View v, int id,Object obj) {
        switch (id){
            case R.id.activity_transport_root:
                finishAllActivity(TransportActivity.class);
                break;
            case R.id.item_transport_pauseIV://Get through
            case R.id.item_transport_downloadIV:
                Transporter downloader=mDownloader;
                Object tag=null!=v?v.getTag():null;
                if (null!=tag&&tag instanceof Transport &&null!=downloader){
                    if (id==R.id.item_transport_pauseIV){
                        downloader.pause(((Transport)tag));
                    }else{
                        downloader.download(((Transport)tag));
                    }
                }
                break;
        }
    }

    @Override
    public void onFileDownloadUpdate(int what, boolean finish, Transport task, Object data) {
        final DownloadAdapter adapter=mAdapter;
        if (finish){
            adapter.remove(task);
        }
        switch (what){
            case START://Get through
            case WAITING://Get through
            case ADD://Get through
            case DOWNLOADING:
                mAdapter.update(task);
                break;
        }
    }

    private void setList(List<Transport> list){
        DownloadAdapter adapter=mAdapter;
        if (null!=adapter){
            adapter.setData(list,true);
        }
    }

    @Override
    protected void onViewAttached(View root) {
        RecyclerView rv=findViewById(R.id.activity_transporting_listRV, RecyclerView.class);
//        List<Transport> list=new ArrayList<>();
//        Transport transport=new Transport("我们都是好孩子.mp3","/volume1/",
//                "Good","ttt",System.currentTimeMillis(),12313,null);
//        list.add(transport);
//        transport=new Transport("灌灌灌灌.mp3","/volume1/","Good","",System.currentTimeMillis(),123123,null);
//        list.add(transport);
//        transport=new Transport("灌灌灌灌sdfasf.mp3","/volume1/","Good","",System.currentTimeMillis(),123123,null);
//        list.add(transport);
//        transport=new Transport("灌灌灌灌sdfasfsdfasf.mp3","/volume1/","Good","",System.currentTimeMillis(),123123,null);
//        list.add(transport);
//        DownloadAdapter adapter=new DownloadAdapter();
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
//        adapter.setData(list);
        rv.setAdapter(mAdapter);
    }
}
