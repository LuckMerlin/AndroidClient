package com.merlin.model;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.merlin.adapter.BaseAdapter;
import com.merlin.adapter.GridSpacingItemDecoration;
import com.merlin.adapter.MediaSheetAdapter;
import com.merlin.client.Client;
import com.merlin.client.OnObjectRequestFinish;
import com.merlin.debug.Debug;
import com.merlin.media.Media;
import com.merlin.media.Sheet;
import com.merlin.server.Frame;
import com.merlin.server.Json;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MediaSheetModel extends DataListModel<Sheet> implements BaseAdapter.OnItemClickListener<Sheet> {
    private final String mCloudAccount;

    public MediaSheetModel(Context context){
        super(context,new MediaSheetAdapter(),new GridLayoutManager(context,3,
                GridLayoutManager.VERTICAL,false),new GridSpacingItemDecoration(3,10,true));
        mCloudAccount="linqiang";
        getAdapter().setOnItemClickListener(this);
        List<Sheet> sheets=new ArrayList<>();
//        sheets.add(new Sheet("曹丹"));
//        sheets.add(new Sheet("曹丹"));
//        sheets.add(new Sheet("曹丹"));
//        sheets.add(new Sheet("曹丹"));
//        sheets.add(new Sheet("曹丹"));
        addData(sheets);
        JSONObject object=new JSONObject();
        putIfNotNull(object,TAG_PAGE,0);
        putIfNotNull(object,TAG_LIMIT,10);
        request(mCloudAccount,"/sheet",object,new OnObjectRequestFinish<List<Sheet>>(){
            @Override
            public void onObjectRequested(boolean succeed, int what, String note, Frame frame, List<Sheet> data) {
                Debug.D(getClass(),"WWWWWWWWWWWWWW "+succeed);
                getAdapter().setData(data);
            }
        });
//        request("linqiang","/sheets/a",0,10,new Client.OnObjectRequestFinish<Sheet>(){
//            @Override
//            public void onObjectRequested(boolean succeed, int what, String note, Frame frame, Sheet data) {
//                Debug.D(getClass(),"的说法 2 "+data);
//            }
//        });
    }

    @Override
    public void onItemClick(View view, int sourceId, int position, Sheet data) {
        String id= null!=data?data.getId():null;
        requestSheetById(id,null);
    }

    private boolean requestSheetById(String sheetId,OnObjectRequestFinish finish){
        if (null==sheetId||sheetId.isEmpty()){
            Debug.W(getClass(),"Can't request media sheet by id."+sheetId);
            return false;
        }
        JSONObject object=new JSONObject();
        putIfNotNull(object,TAG_PAGE,0);
        putIfNotNull(object,TAG_LIMIT,10);
        putIfNotNull(object,TAG_ID,sheetId);
        return null!=request(mCloudAccount,"/sheet/item/"+sheetId,object,new OnObjectRequestFinish<List<Media>>(){
            @Override
            public void onObjectRequested(boolean succeed, int what, String note, Frame frame, List<Media> data) {
                Debug.D(getClass(),"@@@@@@@  "+succeed+" "+data);
            }
        });
    }
}
