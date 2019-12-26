package com.merlin.model;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.activity.TransportActivity;
import com.merlin.adapter.DownloadAdapter;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.task.Download;
import com.merlin.task.DownloadService;
import com.merlin.task.Downloader;

import java.util.List;

public class TransportModel extends BaseModel implements BaseModel.OnModelViewClick, DownloadService.Callback {
    private Downloader mDownloader;
    private final DownloadAdapter mAdapter=new DownloadAdapter();

    public TransportModel(Context context){
        super(context);
        mAdapter.setViewClickListener(this);
    }

    private void test(Downloader downloader){
        String srcPath=null,name,from,target;
        srcPath="./WMDYY.mp3";
        srcPath="./NSWXADGN.mp4";
//        srcPath="/volumes/pythonCodes/linqiang.mp3";
//            srcPath="/volumes/pythonCodes/1576847957749986.mp4";
//            srcPath="/volumes/pythonCodes/1576846797997566.mp4";
//        srcPath="/volumes/pythonCodes/iPartment.S04E01.HDTV.720p.x264.AAC-sherry.mp4";
        from="linqiang";
        name="linqiang_two.mp4";
        target="/sdcard/a";
        Download download=new Download(from,srcPath,name,target,null);
        download.setType(Download.TYPE_NORMAL);
        Debug.D(getClass(),"#####ddddd###### "+name+" "+srcPath);
        downloader.download(download);
        post(()->{
//            Debug.D(getClass(),"开始取消");
//            download.setDeleteIncomplete(false);
//            downloader.pause(download);
        },10000);
        post(()->{
//            Debug.D(getClass(),"重新开始下载");
//            download.setDeleteIncomplete(false);
//            download.setType(Download.TYPE_NORMAL);
//            downloader.download(download);
        },15000);
//        mDownloader.download(test);
    }

    public void setDownloader(Downloader downloader){
        Downloader curr=mDownloader;
        mDownloader=downloader;
        if (null!=downloader){
            downloader.setCallback(this);
            updateList();
            test(downloader);
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
            case R.id.item_transport_pauseIV://Get through
            case R.id.item_transport_downloadIV:
                Downloader downloader=mDownloader;
                Object tag=null!=v?v.getTag():null;
                if (null!=tag&&tag instanceof Download&&null!=downloader){
                    if (id==R.id.item_transport_pauseIV){
                        downloader.pause(((Download)tag));
                    }else{
                        downloader.download(((Download)tag));
                    }
                }
                break;
        }
    }

    @Override
    public void onFileDownloadUpdate(int what, boolean finish, Download task, Object data) {
        if (finish){
            updateList();
        }
        switch (what){
            case START://Get through
            case WAITING://Get through
            case ADD:
                updateList();
                break;
            case DOWNLOADING:
                mAdapter.update(task);
                break;
        }
    }

    private void setList(List<Download> list){
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
