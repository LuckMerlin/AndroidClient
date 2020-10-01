package com.luckmerlin.adapter.recycleview;

import com.luckmerlin.core.proguard.PublishMethods;

import java.util.ArrayList;
import java.util.List;

public class PageData<T> implements PublishMethods {
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

    public final int getFrom() {
        return from;
    }

    public final int getTo() {
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
