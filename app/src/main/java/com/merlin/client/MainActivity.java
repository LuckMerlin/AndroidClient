package com.merlin.client;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.merlin.activity.SocketActivity;
import com.merlin.client.databinding.ActivityFileBrowserBinding;
import com.merlin.model.FileBrowserModel;
import com.merlin.protocol.Tag;


public class MainActivity extends SocketActivity<ActivityFileBrowserBinding, FileBrowserModel> implements Tag {

    public void ddd(View view){
        System.exit(1);
    }

//    private FileBrowserAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        DataListLayout layout=findViewById(R.id.fileBrowser_listDLV);
//        layout.setAdapter(mAdapter=new FileBrowserAdapter());
//        layout.setLayoutManager(new LinearLayoutManager(this));
    }

//    private boolean browser(String path){
//        if (null!=path&&path.length()>0){
//            JSONObject object=new JSONObject();
//            Json.putIfNotNull(object,TAG_COMMAND_TYPE,TAG_COMMAND_LIST_DIR);
//            Json.putIfNotNull(object,TAG_FILE,path);
//            return sendMessage(object.toString(), "linqiang", TAG_MESSAGE_QUERY, new Socket.OnRequestFinish() {
//                @Override
//                public void onRequestFinish(boolean succeed, int what, Frame frame) {
//                    if (succeed){
//                        String data=null!=frame?frame.getBodyText():null;
//                        List<File> list=null!=data&&data.length()>0? JSON.parseArray(data, File.class):null;
//                        Debug.D(getClass(),"成都 "+(null!=list?list.size():-1));
//                        new Handler(Looper.getMainLooper()).post(new Runnable() {
//                            @Override
//                            public void run() {
//                                mAdapter.setData(list,true);
//                            }
//                        });
//                    }
//                }
//            });
//        }
//        Debug.W(getClass(),"Can't browser path which is invalid."+path);
//        return false;
//    }

}
