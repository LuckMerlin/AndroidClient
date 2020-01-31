package com.merlin.binding;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.adapter.BaseAdapter;
import com.merlin.classes.Classes;

public final class Adapter {
    private final String mAdapterClass;

    private Adapter(String adapterClass){
        mAdapterClass=adapterClass;
    }

    public static Adapter adapter(String adapterClass){
        return new Adapter(adapterClass);
    }

    public boolean inflate(RecyclerView rv){
        String cls=mAdapterClass;
        Object instance=null!=cls&&null!=rv?new Classes().instance(cls,rv.getContext()):null;
        if (null!=instance&&instance instanceof BaseAdapter){
            RecyclerView.LayoutManager lm=instance instanceof BaseAdapter ? ((BaseAdapter)instance).onResolveLayoutManager(rv):null;
            if (null!=lm){
                rv.setLayoutManager(lm);
            }
            if (instance instanceof BaseAdapter.OnAdapterBind){
                ((BaseAdapter.OnAdapterBind)instance).onAdapterBind(rv);
            }
            rv.setAdapter((BaseAdapter)instance);
            return true;
        }
        return false;
    }

    public String getAdapterClass() {
        return mAdapterClass;
    }


}
