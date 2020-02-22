package com.merlin.model;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.merlin.adapter.BaseAdapter;
import com.merlin.adapter.LinearItemDecoration;
import com.merlin.adapter.SheetMediaAdapter;
import com.merlin.bean.NasMedia;
import com.merlin.bean.MediaSheet;
import com.merlin.bean.User;

import java.util.ArrayList;
import java.util.List;

public class ActivityMediaSheetDetailModel extends DataListModel<NasMedia> implements
        BaseAdapter.OnItemClickListener<NasMedia>, BaseModel.OnIntentChanged {
    private final ObservableField<MediaSheet> mSheet=new ObservableField<>();

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ActivityMediaSheetDetailModel(Context context){
        super(context,new SheetMediaAdapter(),new LinearLayoutManager(context), new LinearItemDecoration(8));
        MediaSheet sheet=new MediaSheet();
        sheet.setTitle("流行音乐 ");
        sheet.setImageUrl("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=490485645,758785298&fm=26&gp=0.jpg");
        sheet.setNote("这是我们撒旦发射点噶是噶是噶 ");
        User user=new User();
        user.setName("LuckMerlin");
        sheet.setCreateUser(user);
        mSheet.set(sheet);
        //
        List<NasMedia> list=new ArrayList<>();
        for (int i = 0; i < 50; i++) {
//            NasMedia media=new NasMedia();
//            media.setTitle("我们");
//            list.add(media);
        }
        getAdapter().setData(list);
    }

    @Override
    public void onIntentChange(Intent intent) {
        MediaSheet sheet=null!=intent?getDataFromIntent(intent, MediaSheet.class):null;
//        String sheetId=null!=sheet?sheet.getSheetId():null;
//        toast("嘻哈十大 "+sheetId);
//        if (null!=sheetId&&!sheetId.isEmpty()){
//            mSheet.set(sheet);
//        }
    }

    @Override
    public void onItemClick(View view, int sourceId, int position, NasMedia data) {
        if (null!=data){
//            MediaPlayService.add(view.getContext(),data,-1);
        }
    }

    public ObservableField<MediaSheet> getSheet() {
        return mSheet;
    }
}
