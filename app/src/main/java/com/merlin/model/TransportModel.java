package com.merlin.model;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.merlin.adapter.TransportingAdapter;
import com.merlin.transport.Transport;

import java.util.ArrayList;
import java.util.List;

public class TransportModel extends DataListModel {

    public TransportModel(Context context){
        super(context,new TransportingAdapter(),new LinearLayoutManager(context));
    }


    @Override
    protected void onViewAttached(View root) {
        List<Transport> list=new ArrayList<>();
        Transport transport=new Transport("我们都是好孩子.mp3","/volume1/","Good",System.currentTimeMillis());
        list.add(transport);
        transport=new Transport("灌灌灌灌.mp3","/volume1/","Good",System.currentTimeMillis());
        list.add(transport);
        transport=new Transport("帝发誓.mp3","/volume1/","Good",System.currentTimeMillis());
        list.add(transport);
        setData(list,true);
    }
}
