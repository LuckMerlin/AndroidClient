package com.merlin.browser;
import com.merlin.bean.Document;

import java.util.ArrayList;
import java.util.Collection;

public final class Collector<T extends Document> extends ArrayList<T>{
    private Class<T> mTargetClass;

    public Collector(Collection<T> data){
        if (null!=data&&data.size()>0&&addAll(data)){
           //Do nothing
        }
    }

    public Collector(Class<T> cls){
        this(cls,null);
    }

    public Collector(Class<T> cls,T data){
        mTargetClass=cls;
        add(data);
    }

    public final boolean add(T t,String debug) {
        return null!=t&&add(t);
    }

    public final boolean remove(Object t,String debug) {
        return null!=t&&remove(t);
    }

    @Override
    public final boolean add(T t) {
        Class<T> curr=mTargetClass;
        if (null==t||(null!=curr&&!curr.equals(t.getClass()))){
            return false;
        }
        mTargetClass=null!=mTargetClass?mTargetClass:(Class<T>) t.getClass();
        return super.add(t);
    }

    public Class<T> getTargetClass() {
        return mTargetClass;
    }

    public boolean isTargetClassEqual(Class<?> cls){
        Class<T> curr=mTargetClass;
        return (null==cls&&null==cls)||(null!=cls&&null!=curr&&curr.equals(cls));
    }

    public final ArrayList<T> getFiles(Class<T> cls) {
        Class<T> target=mTargetClass;
        if (null!=cls&&(null==target||!target.equals(cls))){
            return null;
        }
        int size=size();
        ArrayList<T> data=size>0?new ArrayList<>(size):null;
        return null!=data&&data.addAll(this)?data:null;
    }
}