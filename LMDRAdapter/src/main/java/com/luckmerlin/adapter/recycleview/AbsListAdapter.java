package com.luckmerlin.adapter.recycleview;

public class AbsListAdapter<T> extends ListAdapter<T>{

    public boolean loadPre(){

        return false;
    }

    public boolean loadNext(){

        return false;
    }

}
