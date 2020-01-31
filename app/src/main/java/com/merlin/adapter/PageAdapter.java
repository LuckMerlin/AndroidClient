package com.merlin.adapter;

import androidx.databinding.ViewDataBinding;

import com.merlin.api.PageData;

import java.util.List;

public abstract class  PageAdapter<T,V extends ViewDataBinding> extends BaseAdapter<T,V> {
//    private PageData<T> mLatestPage;
//    private PageQuery<M> mQuerying;
//
//    public final boolean query(PageQuery<M> query){
//        if (null==query){
//            return false;
//        }
//        PageQuery<M> current=mQuerying;
//        if (null!=current&&current.equals(query)){
//            return false;
//        }
//        mQuerying=query;
//
//        return false;
//    }

    public final boolean fillPage(PageData<T> page){
        List<T> list=null!=page?page.getData():null;
        if (null!=list&&list.size()>0){
//            mLatestPage=page;
            if (page.getPage()<=0){
                setData(list, true);
            }else{
                addAll(list, true);
            }
        }
        return false;
    }

}
