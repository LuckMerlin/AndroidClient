package com.merlin.browser;

import androidx.annotation.Nullable;

import com.merlin.bean.Path;

import java.util.ArrayList;
import java.util.Collection;

public final class Collector<T extends Path> extends ArrayList<T>{
    private final Integer mMax;
    private Class<T> mTargetClass;

    public Collector(Collection<T> data){
        mMax=null;
        if (null!=data&&data.size()>0&&addAll(data)){
           //Do nothing
        }
    }

    public Collector(Integer max,Class<T> cls){
        this(max,cls,null);
    }

    public Collector(Integer max,Class<T> cls, T data){
        mMax=max;
        mTargetClass=cls;
        add(data);
    }

    public final boolean add(T t,String debug) {
        return null!=t&&add(t);
    }

    public final boolean remove(Object t,String debug) {
        return null!=t&&remove(t);
    }

    public T indexOfChild(Object obj) {
        int size=null!=obj?indexOf(obj):-1;
        return size>=0&&size<size()?get(size):null;
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
        ArrayList<T> data=size>0?new ArrayList<T>(size):null;
        return null!=data&&data.addAll(this)?data:null;
    }

    public Integer getMax() {
        return mMax;
    }
}