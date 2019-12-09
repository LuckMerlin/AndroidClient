package com.merlin.model;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.merlin.adapter.BaseAdapter;
import com.merlin.adapter.FileBrowserAdapter;
import com.merlin.bean.File;
import com.merlin.client.Client;
import com.merlin.debug.Debug;
import com.merlin.oksocket.OnFrameReceive;
import com.merlin.oksocket.Socket;
import com.merlin.protocol.Tag;
import com.merlin.protocol.What;
import com.merlin.server.Frame;
import com.merlin.server.Json;
import com.merlin.server.Response;

import java.util.List;


public class FileBrowserModel extends DataListModel implements SwipeRefreshLayout.OnRefreshListener, BaseAdapter.OnItemClickListener,OnFrameReceive, Tag {
    private String mCurrPath="/volume1/pythonCodes/LuckMerlin";//TAG_PATH_HOME;
    private String mLoadingPath=null;

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
    public void onItemClick(View view, File bean) {
          if (null!=bean){
                if (!bean.isRead()){
                    toast("文件不可读");
                }else if (bean.isIsFile()){
                    toast("点击了文件"+bean.getName());
                }else{
                    browser(bean.getPath());
                }
          }
    }

    public void back(View view){
        browserParent();
    }

    public boolean browserParent(){
        String path=mCurrPath;
        java.io.File parent=null!=path?new java.io.File(path).getParentFile():null;
        String parentPath=null!=parent?parent.getAbsolutePath():null;
        if (null!=parentPath&&parentPath.length()>0){
            return browser(parentPath);
        }
        return false;
    }

    @Override
    public void onRefresh() {
        browser(mCurrPath);//Browser current path again
    }

    @Override
    public void onFrameReceived(Frame frame, Client client) {
        Response response=null!=frame?frame.getResponse():null;
        if (null!=response &&response.isSucceed()&&response.getWhat()== What.WHAT_SUCCEED){//Test
            String note=response.getNote();
            if (null!=note&&note.contains("wuyue")){
                 browser(mCurrPath);
            }
        }
    }

    private boolean browser(String path){
        if (null!=path&&path.length()>0){
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
                        mCurrPath=path;
                        String data=null!=frame?frame.getBodyText():null;
                        List<File> list=null!=data&&data.length()>0? JSON.parseArray(data, File.class):null;
                        setData(list,true);
                    }
                }
            });
        }
        Debug.W(getClass(),"Can't browser path which is invalid."+path);
        return false;
    }

}
