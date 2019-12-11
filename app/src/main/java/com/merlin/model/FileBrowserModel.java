package com.merlin.model;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.merlin.adapter.BaseAdapter;
import com.merlin.adapter.FileBrowserAdapter;
import com.merlin.bean.FileBrowserMeta;
import com.merlin.bean.FileMeta;
import com.merlin.client.Client;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.oksocket.OnFrameReceive;
import com.merlin.oksocket.Socket;
import com.merlin.player.MediaPlayer;
import com.merlin.protocol.Tag;
import com.merlin.protocol.What;
import com.merlin.server.Frame;
import com.merlin.server.Json;
import com.merlin.server.Response;

import java.util.List;


public class FileBrowserModel extends DataListModel implements SwipeRefreshLayout.OnRefreshListener, BaseAdapter.OnItemClickListener,OnFrameReceive, Tag {
    private String mLoadingPath=null,mParentPath;
    private final ObservableField<String> mCurrPath=new ObservableField<>("");

    public FileBrowserModel(Context context){
        super(context,new FileBrowserAdapter(),new LinearLayoutManager(context));
    }

    @Override
    public void onBridgeBoundChange(boolean bound) {
        if (bound){
            setColorSchemeColors(Color.RED,Color.YELLOW,Color.BLUE);
            setProgressBackgroundColorSchemeColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onItemClick(View view, int sourceId, Object data) {
       if (null!=data&&data instanceof FileMeta){
           FileMeta file=(FileMeta)data;
            if (!file.isRead()){
                toast("文件不可读");
            }else if (file.isDirectory()){
                browser(file.getFile());
            }else{
                toast("点击了文件"+file.getName());
            }
       }
    }

    @Override
    public void onViewClick(View v, int id) {
        super.onViewClick(v, id);
        switch (id){
            case R.id.fileBrowser_topBackIV:
                browserParent();
                break;
            case R.id.fileBrowser_menuIV:
                toast("点击了菜单");
                break;
            case R.id.fileBrowser_topSearchIV:
                toast("点击了搜索");
                break;
        }
    }

    public boolean browserParent(){
        return null!=mParentPath&&browser(mParentPath);
    }

    @Override
    public void onRefresh() {
        browser(mCurrPath.get());//Browser current path again
    }


    public ObservableField<String> getCurrentPath() {
        return mCurrPath;
    }

    @Override
    public void onFrameReceived(Frame frame, Client client) {
        Response response=null!=frame?frame.getResponse():null;
        if (null!=response &&response.isSucceed()&&response.getWhat()== What.WHAT_SUCCEED){//Test
            String note=response.getNote();
            if (null!=note&&note.contains("wuyue")){
//                 browser(mCurrPath.get());
                openFile(null);
            }
        }
    }

    public static Context MM;

    private boolean openFile(String path){
//        path = "F:\\LuckMerlin\\SLManager\\test.png";
        path = "C:\\Users\\admin\\Desktop\\linqiang.mp3";
        JSONObject object=new JSONObject();
        Json.putIfNotNull(object,TAG_COMMAND_TYPE,TAG_COMMAND_READ_FILE);
        Json.putIfNotNull(object,TAG_FILE,path);
        Handler handler=new Handler(Looper.getMainLooper());
        MediaPlayer player=new MediaPlayer();
        return sendMessage(object.toString(), "linqiang", TAG_MESSAGE_QUERY,30*1000,new Socket.OnRequestFinish() {
            @Override
            public void onRequestFinish(boolean succeed, int what, Frame frame) {
                if (succeed&&null!=frame){
                  handler.post(new Runnable() {
                      @Override
                      public void run() {
//                          player.play(frame);
                      }
                  });
                }
//                Debug.D(getClass(),"收到 真 "+succeed++" "+(null!=frame?frame.getBodyBytesLength():-1));
                //                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        byte[] bytes=frame.getBodyBytes();
//                        Dialog dialog=new Dialog(MM);
//                        ImageView imageView=new ImageView(MM);
//                        imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
//                        dialog.setContentView(imageView);
//                        dialog.show();
//                    }
//                });
            }
        });
    }

    public final boolean refreshCurrentPath(){
        browser(mCurrPath.get());
        return false;
    }

    private boolean browser(String path){
        if (null!=path){
            JSONObject object=new JSONObject();
            Json.putIfNotNull(object,TAG_COMMAND_TYPE,TAG_COMMAND_LIST_DIR);
            Json.putIfNotNull(object,TAG_FILE,path);
            setRefreshing(true);
            mLoadingPath=path;
            return sendMessage(object.toString(), "linqiang", TAG_MESSAGE_QUERY, new Socket.OnRequestFinish() {
                @Override
                public void onRequestFinish(boolean succeed, int what, Frame frame) {
                    if (null!=mLoadingPath){
                        synchronized (mLoadingPath){
                            if (mLoadingPath.equals(path)){
                                mLoadingPath=null;
                                setRefreshing(false);
                            }
                        }
                    }
                    if (succeed){
                        String data=null!=frame?frame.getBodyText():null;
                        FileBrowserMeta meta=null!=data&&data.length()>0? JSON.parseObject(data, FileBrowserMeta.class):null;
                        if (null!=meta){
                            if (meta.isDirectory()){
                                mCurrPath.set(meta.getFile());
                                mParentPath=meta.getParent();
                                List<FileMeta> list=null!=meta?meta.getData():null;
                                Debug.D(getClass(),"大小 "+(null!=list?list.size():-1));
                                setData(list,true);
                            }else{
                                Debug.D(getClass(),"这是一个文件啊 ");
                            }
                        }
                    }
                }
            });
        }
        Debug.W(getClass(),"Can't browser path which is invalid."+path);
        return false;
    }

}
