package com.luckmerlin.databinding;

import java.util.ArrayList;
import java.util.Collection;

public class Bindings<T extends IBinding> implements IBinding {
    private Collection<T> mBindings;

    public Bindings(Collection<T> collection){
        mBindings=collection;
    }

    public final Bindings append(T ...objects){
        return append(true,objects);
    }

    public final Bindings append(boolean skipExist, T ...objects){
        if (null!=objects&&objects.length>0){
            for (T child:objects) {
                if (null!=child&&(!contains(child)||!skipExist)){
                    add(child);
                }
            }
        }
        return this;
    }

    public final Bindings add(T child){
        if (null!=child){
            Collection<T> bindings=mBindings;
            bindings=null!=bindings?bindings:(mBindings=new ArrayList<>());
            bindings.add(child);
        }
        return this;
    }

    public final Bindings remove(T child){
        if (null!=child){
            Collection<T> bindings=mBindings;
            if (null!=bindings){
                bindings.remove(child);
                if (bindings.size()<=0){
                    mBindings=null;
                }
            }
        }
        return this;
    }

    public final Collection<T> getBindings() {
        return mBindings;
    }

    public final boolean contains(Object child){
        Collection<T> bindings=null!=child?mBindings:null;
        return null!=bindings&&bindings.contains(child);
    }

}
