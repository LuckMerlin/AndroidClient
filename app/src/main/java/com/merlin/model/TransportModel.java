package com.merlin.model;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.adapter.TransportingAdapter;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.transport.Transport;

import java.util.ArrayList;
import java.util.List;

public class TransportModel extends BaseModel {

    public TransportModel(Context context){
        super(context);
    }


    @Override
    protected void onViewAttached(View root) {
        RecyclerView rv=findViewById(R.id.activity_transporting_listRV, RecyclerView.class);
        List<Transport> list=new ArrayList<>();
        Transport transport=new Transport("我们都是好孩子.mp3","/volume1/","Good",System.currentTimeMillis());
        list.add(transport);
        transport=new Transport("灌灌灌灌.mp3","/volume1/","Good",System.currentTimeMillis());
        list.add(transport);
        transport=new Transport("帝发誓.mp3","/volume1/","Good",System.currentTimeMillis());
        list.add(transport);
        Debug.D(getClass(),"@@@@@@@@@@@@@@@@@@@ "+rv);
    }
}
