package com.merlin.model;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;

import com.merlin.adapter.MediaSheetAdapter;
import com.merlin.client.Client;
import com.merlin.media.Sheet;
import com.merlin.server.Frame;

import java.util.ArrayList;
import java.util.List;

public class MediaSheetModel extends DataListModel<Sheet>{

    public MediaSheetModel(Context context){
        super(context,new MediaSheetAdapter(),new GridLayoutManager(context,3,
                GridLayoutManager.VERTICAL,false));
        List<Sheet> sheets=new ArrayList<>();
        sheets.add(new Sheet("曹丹"));
        sheets.add(new Sheet("曹丹"));
        sheets.add(new Sheet("曹丹"));
        sheets.add(new Sheet("曹丹"));
        sheets.add(new Sheet("曹丹"));
        addData(sheets);
        request("linqiang","/sheets/a",0,10,new Client.OnObjectRequestFinish<List<Sheet>>(){
            @Override
            public void onObjectRequested(boolean succeed, int what, String note, Frame frame, List<Sheet> data) {

            }
        });
    }




}
