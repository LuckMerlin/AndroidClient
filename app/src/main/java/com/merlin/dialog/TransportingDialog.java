package com.merlin.dialog;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.adapter.TransportingAdapter;
import com.merlin.client.R;

public class TransportingDialog extends Dialog {

    public TransportingDialog(Context context){
        super(context);
        setContentView(R.layout.dialog_transporting);
        RecyclerView recyclerView=findViewById(R.id.dialog_transporting_listRV, RecyclerView.class);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new TransportingAdapter());
    }
}
