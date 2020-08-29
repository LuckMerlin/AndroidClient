package com.luckmerlin.databinding.adapter;

import java.util.ArrayList;
import java.util.List;

public class PageData<T> {
    private long length;
    private int from;
    private ArrayList<T> data;

    public PageData(){
        this(0,null,-1);
    }

    public PageData(int from, ArrayList<T> data, long length){
        this.from=from;
        this.length=length;
        this.data=data;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        List<T>  list=from>=0?data:null;
        return from+(null!=list?list.size():0);
    }

    public final ArrayList<T> getData() {
        return data;
    }

    public final long getLength() {
        return length;
    }

}