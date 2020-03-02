package com.merlin.adapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.transport.Transport;

public class TransportAdapter<T extends Transport> extends Adapter<T> {


    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        LinearLayoutManager llm=new LinearLayoutManager(rv.getContext(), RecyclerView.VERTICAL,false);
        llm.setSmoothScrollbarEnabled(true);
        return llm;
    }
}
