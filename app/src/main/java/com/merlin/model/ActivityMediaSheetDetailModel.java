package com.merlin.model;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.merlin.activity.MediaSheetDetailActivity;
import com.merlin.adapter.BaseAdapter;
import com.merlin.adapter.GridSpacingItemDecoration;
import com.merlin.adapter.LinearItemDecoration;
import com.merlin.adapter.MediaSheetAdapter;
import com.merlin.adapter.SheetMediaAdapter;
import com.merlin.client.OnObjectRequestFinish;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.media.Media;
import com.merlin.media.MediaPlayService;
import com.merlin.media.Sheet;
import com.merlin.server.Frame;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityMediaSheetDetailModel extends DataListModel<Media> implements
        BaseAdapter.OnItemClickListener<Media>, BaseModel.OnIntentChanged {
    private final ObservableField<Sheet> mSheet=new ObservableField<>();

    public ActivityMediaSheetDetailModel(Context context){
        super(context,new SheetMediaAdapter(),new LinearLayoutManager(context), new LinearItemDecoration(8));
//        Sheet sheet=new Sheet();
//        sheet.setName("手动阀手动阀 ");
//        sheet.setAccount("linqiang");
//        sheet.setId("手动阀手动阀 ");
//        sheet.setSize(12141);
//        sheet.setCreate(12141);
//        loadSheet(sheet);
    }

    @Override
    public void onIntentChange(Intent intent) {
        Sheet sheet=null!=intent?getDataFromIntent(intent,Sheet.class):null;
        String sheetId=null!=sheet?sheet.getId():null;
        if (null!=sheetId&&!sheetId.isEmpty()){
            mSheet.set(sheet);
            loadSheet(sheet);
        }else{
            Debug.W(getClass(),"Media sheet id is None."+sheet);
        }
    }

    @Override
    public void onItemClick(View view, int sourceId, int position, Media data) {
        if (null!=data){
            MediaPlayService.add(view.getContext(),data,-1);
        }
    }

    private boolean loadSheet(Sheet sheet){
        String sheetId=null!=sheet?sheet.getId():null;
        if (null==sheetId||sheetId.isEmpty()){
            Debug.W(getClass(),"Can't request media sheet by id."+sheetId);
            return false;
        }
        mSheet.set(sheet);
        String account=sheet.getAccount();
        JSONObject object=new JSONObject();
        putIfNotNull(object,TAG_PAGE,0);
        putIfNotNull(object,TAG_LIMIT,10);
        putIfNotNull(object,TAG_ID,sheetId);
        return null!=request(account, "/sheet/item/" + sheetId, object, new OnObjectRequestFinish<List<Media>>() {
            @Override
            public void onObjectRequested(boolean succeed, int what, String note, Frame frame, List<Media> data) {
                if (succeed){
                    getAdapter().setData(data);
                }else{
                    toast(R.string.requestFail);
                }
            }
        });
    }

    public ObservableField<Sheet> getSheet() {
        return mSheet;
    }
}
