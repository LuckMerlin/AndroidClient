package com.merlin.client;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.activity.SocketActivity;
import com.merlin.adapter.FileBrowserAdapter;
import com.merlin.client.databinding.ActivityFileBrowserBinding;
import com.merlin.debug.Debug;
import com.merlin.model.FileBrowserModel;
import com.merlin.oksocket.OnFrameReceive;
import com.merlin.oksocket.Socket;
import com.merlin.protocol.Tag;
import com.merlin.protocol.What;
import com.merlin.server.Frame;
import com.merlin.server.Json;
import com.merlin.server.Response;

import org.json.JSONObject;


public class MainActivity extends SocketActivity<ActivityFileBrowserBinding, FileBrowserModel> implements Tag,OnFrameReceive {

    public void ddd(View view){
        System.exit(1);
    }

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView=findViewById(R.id.fileBrowser_listRV);
        mRecyclerView.setAdapter(new FileBrowserAdapter());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private boolean browser(String path){
        if (null!=path&&path.length()>0){
            JSONObject object=new JSONObject();
            Json.putIfNotNull(object,TAG_COMMAND_TYPE,TAG_COMMAND_LIST_DIR);
            Json.putIfNotNull(object,TAG_FILE,path);
            return sendMessage(object.toString(), "linqiang", TAG_MESSAGE_QUERY, new Socket.OnRequestFinish() {
                @Override
                public void onRequestFinish(boolean succeed, int what, Frame frame) {
//                    String data=null!=frame?frame.getBodyText():null;
                    Debug.D(getClass(),"成都 "+frame);
//                  JSONArray array=JSON.parseArray(data);
//                    Debug.D(getClass(),"@@@@@@@@@@ "+(null!=array?array.size():-1)+ data);
                }
            });
        }
        Debug.W(getClass(),"Can't browser path which is invalid."+path);
        return false;
    }

    @Override
    public void onFrameReceived(Frame frame, Client client) {
        Response response=null!=frame?frame.getResponse():null;
        if (null!=response &&response.isSucceed()&&response.getWhat()== What.WHAT_SUCCEED){//Test
             String note=response.getNote();
             if (null!=note&&note.contains("wuyue")){
                 browser(TAG_PATH_HOME);
             }
//             Debug.D(getClass(),"dddd "+note);
//            browser(TAG_PATH_HOME);
//            Debug.D(getClass(),"Go to file browser activity .");
//            RecyclerView recyclerView=;
//            try {
//                startActivity(new Intent(this, FileBrowserActivity.class));
//            }catch (Exception e){
//                Debug.E(getClass(),"dddd "+e,e);
//            }
        }
    }
}
