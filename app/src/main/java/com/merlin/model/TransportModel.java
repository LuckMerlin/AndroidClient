package com.merlin.model;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.activity.TransportActivity;
import com.merlin.adapter.TransportAdapter;
import com.merlin.client.R;
import com.merlin.transport.Transport;

import java.util.ArrayList;
import java.util.List;

public class TransportModel extends BaseModel implements BaseModel.OnModelViewClick {

    public TransportModel(Context context){
        super(context);
    }


    @Override
    public void onViewClick(View v, int id) {
        switch (id){
            case R.id.activity_transport_root:
                finishAllActivity(TransportActivity.class);
                break;
        }
    }

    @Override
    protected void onViewAttached(View root) {
        RecyclerView rv=findViewById(R.id.activity_transporting_listRV, RecyclerView.class);
        List<Transport> list=new ArrayList<>();
        Transport transport=new Transport("我们都是好孩子.mp3","/volume1/",
                "Good","ttt",System.currentTimeMillis(),12313,null);
        list.add(transport);
        transport=new Transport("灌灌灌灌.mp3","/volume1/","Good","",System.currentTimeMillis(),123123,null);
        list.add(transport);
        transport=new Transport("灌灌灌灌sdfasf.mp3","/volume1/","Good","",System.currentTimeMillis(),123123,null);
        list.add(transport);
        transport=new Transport("灌灌灌灌sdfasfsdfasf.mp3","/volume1/","Good","",System.currentTimeMillis(),123123,null);
        list.add(transport);
        TransportAdapter adapter=new TransportAdapter();
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        adapter.setData(list);
        rv.setAdapter(adapter);
    }
}
