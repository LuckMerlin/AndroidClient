package com.merlin.model;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.activity.TransportActivity;
import com.merlin.adapter.DownloadAdapter;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.task.DownloadService;
import com.merlin.task.DownloadTask;
import com.merlin.task.Downloader;

import java.util.List;

public class TransportModel extends BaseModel implements BaseModel.OnModelViewClick, DownloadService.Callback {
    private Downloader mDownloader;
    private final DownloadAdapter mAdapter=new DownloadAdapter();

    public TransportModel(Context context){
        super(context);
    }

    public void setDownloader(Downloader downloader){
        Downloader curr=mDownloader;
        mDownloader=downloader;
        if (null!=downloader){
            downloader.setCallback(this);
            updateList();
        }else if(null!=curr){
            curr.setCallback(null);
        }
    }

    private void updateList(){
        Downloader downloader=mDownloader;
        setList(null!=downloader?downloader.getDownloadList():null);
    }

    @Override
    public void onViewClick(View v, int id) {
        switch (id){
            case R.id.activity_transport_root:
                finishAllActivity(TransportActivity.class);
                break;
        }
    }

    @Override
    public void onFileDownloadUpdate(int what, boolean finish, DownloadTask task, Object data) {
        if (finish){
            updateList();
        }
        switch (what){
            case START:
                updateList();
                break;
            case DOWNLOADING:
                mAdapter.update(task);
                break;
        }
    }

    private void setList(List<DownloadTask> list){
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
