package com.merlin.adapter;

import com.merlin.api.PageData;

import java.util.List;

public abstract class MultiPageAdapter<T> extends Adapter<T> {

    public final boolean fillPage(PageData<T> page){
        List<T> list=null!=page?page.getData():null;
        if (null!=list&&list.size()>0){
            if (page.getPage()<=0){
                setData(list);
            }else{
                append(true,list);
            }
        }
        return false;
    }

}
